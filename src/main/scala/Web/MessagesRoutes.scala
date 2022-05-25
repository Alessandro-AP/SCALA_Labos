// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 24.05.22

package Web

import Chat.{AnalyzerService, ExprTree, Parser, TokenizerService}
import Services.{AccountService, MessageService, Session, SessionService}
import Services.MessageService.MESSAGES_LIMIT
import Web.Layouts.HtmlTag

import scala.collection.mutable.Set
import castor.Context.Simple.global
import ujson.Obj

import scala.collection.mutable

/**
  * Assembles the routes dealing with the message board:
  * - One route to display the home page
  * - One route to send the new messages as JSON
  * - One route to subscribe with websocket to new messages
  */
class MessagesRoutes(tokenizerSvc: TokenizerService,
                     analyzerSvc: AnalyzerService,
                     msgSvc: MessageService,
                     accountSvc: AccountService,
                     sessionSvc: SessionService)(implicit val log: cask.Logger) extends cask.Routes:
    import Decorators.getSession

    // Message constants
    private val SUCCESS = "success"
    private val ERR = "err"
    private val ERR_NOT_BLANK = "Message cannot be empty or blank !"
    private val ERR_LOGIN = "Please log in first !"
    private val ERR_INVALID_CMD = "Invalid bot command!"
    private val BOT = "Bot"

    // Websockets active connexions.
    private val openConnections =  mutable.Set.empty[cask.WsChannelActor]

    /**
      * Display the home page (with the message board and the form to send new messages).
      * @param session Current session
      * @return Homepage
      */
    @getSession(sessionSvc) // This decorator fills the `(session: Session)` part of the `index` method.
    @cask.get("/")
    def index()(session: Session): HtmlTag = {
        Layouts.homepage(session.getCurrentUser, msgSvc.getLatestMessages(MESSAGES_LIMIT))
    }

    /**
      * Process the new messages sent as JSON object to `/send`.
      * @param msg Message sent
      * @param session Current session
      * @return a JSON object indicating a success or an error with a message.
      *         Error in case of :
      *         - No user is logged in
      *         - The message is empty
      */
    @getSession(sessionSvc)
    @cask.postJson("/send")
    def send(msg: String)(session: Session): Obj = {
        if msg.isBlank then ujson.Obj(SUCCESS -> false, ERR -> ERR_NOT_BLANK)
        else if session.getCurrentUser.isEmpty then ujson.Obj(SUCCESS -> false, ERR -> ERR_LOGIN)
        else if msg.startsWith("@") then handleMention(msg, session)
        else notifyNewMsg(session.getCurrentUser.get, msg)
    }

    /**
      * Process and store the new websocket connection made to `/subscribe`.
      */
    @cask.websocket("/subscribe")
    def subscribe(): cask.WebsocketResult = {
        cask.WsHandler { connection =>
            openConnections += connection
            cask.WsActor {
                case cask.Ws.Text(_) => sendLatestMsg(connection)
                case cask.Ws.Close(_, _) => openConnections -= connection
            }
        }
    }

    /**
      * Delete the message history when a GET is made to `/clearHistory`.
      */
    @getSession(sessionSvc)
    @cask.get("/clearHistory")
    def clearHistory()(session: Session): HtmlTag = {
        msgSvc.deleteHistory()
        Layouts.homepage(session.getCurrentUser)
    }

    /**
      * Send the MESSAGES_LIMIT latest messages on the provided connection.
      * @param connection between two hosts
      */
    def sendLatestMsg(connection: cask.WsChannelActor): Unit = {
        connection.send(cask.Ws.Text(
          Layouts.msgList(msgSvc.getLatestMessages(MessageService.MESSAGES_LIMIT)).render)
        )
    }

    /**
      * Save an incoming message and notify all other users of the newest message.
      * @param user Author of message
      * @param msg Message
      * @param mention Mention of a user or bot if provided
      * @param exprType ExprTree of a command if provided
      * @param replyToId User id to which we reply
      * @return a JSON object indicating a success msg.
      */
    private def notifyNewMsg(user: String, msg: String, mention: Option[String] = None, exprType: Option[ExprTree] = None, replyToId: Option[Long] = None) = {
        msgSvc.add(user, Layouts.msgContent(msg), mention, exprType, replyToId)
        openConnections.foreach(sendLatestMsg)
        ujson.Obj(SUCCESS -> true, ERR -> "")
    }

    /**
      * When the message contains an @ it is treated as a mention.
      * If the mention is addressed to the bot, it will process the message and provide a reply.
      * Otherwise, a user is mentioned.
      *
      * @param msg Message to be sent
      * @param session Current Session
      * @return a JSON object indicating a success or an error with a message.
      */
    private def handleMention(msg: String, session: Session) = {
        val mention = if msg.contains(" ") then msg.substring(1, msg.indexOf(" ")) else msg.substring(1) // check mention format
        if mention.toLowerCase == BOT.toLowerCase then
            handleBot(msg, session)
        else
            notifyNewMsg(session.getCurrentUser.get, msg, Some(mention))
    }

    /**
      * Process the messages sent to the bot. The message and its reply from the bot
      * will be added to the message store together.
      * @param msg Message sent to the bot
      * @param session Current session
      * @return a JSON object indicating a success or an error with a message.
      */
    private def handleBot(msg: String, session: Session) = {
        try {
            val tokenized = tokenizerSvc.tokenize(msg.substring(4).trim.toLowerCase)
            val expr = Parser(tokenized).parsePhrases()
            if expr.isInstanceOf[ExprTree.Login] then throw Chat.UnexpectedTokenException("Not allowed here")
            val id = msgSvc.add(session.getCurrentUser.get, Layouts.msgContent(msg))
            openConnections.foreach(sendLatestMsg)
            notifyNewMsg(BOT, analyzerSvc.reply(session)(expr), None, Some(expr), Some(id))
        } catch {
            case _: Chat.UnexpectedTokenException => ujson.Obj(SUCCESS -> false, ERR -> ERR_INVALID_CMD)
        }
    }

    initialize()
end MessagesRoutes