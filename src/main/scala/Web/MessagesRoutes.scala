// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 24.05.22

package Web

import Chat.{AnalyzerService, Parser, TokenizerService, ExprTree}
import Data.{AccountService, MessageService, Session, SessionService}

import scala.collection.mutable.Set
import castor.Context.Simple.global
import ujson.Obj

import scala.collection.mutable

/**
  * Assembles the routes dealing with the message board:
  * - One route to display the home page
  * - One route to send the new messages as JSON
  * - One route to subscribe with websocket to new messages
  *
  * @param log
  */
class MessagesRoutes(tokenizerSvc: TokenizerService,
                     analyzerSvc: AnalyzerService,
                     msgSvc: MessageService,
                     accountSvc: AccountService,
                     sessionSvc: SessionService)(implicit val log: cask.Logger) extends cask.Routes:
    import Decorators.getSession

    @getSession(sessionSvc) // This decorator fills the `(session: Session)` part of the `index` method.
    @cask.get("/")
    def index()(session: Session) =
        Layouts.homepage(session.getCurrentUser) //TODO shows messages first time that we connect to homepage???


    // TODO - Part 3 Step 4b: Process the new messages sent as JSON object to `/send`. The JSON looks
    //      like this: `{ "msg" : "The content of the message" }`.
    //
    //      A JSON object is returned. If an error occurred, it looks like this:
    //      `{ "success" : false, "err" : "An error message that will be displayed" }`.
    //      Otherwise (no error), it looks like this:
    //      `{ "success" : true, "err" : "" }`
    //
    //      The following are treated as error:
    //      - No user is logged in
    //      - The message is empty
    //
    //      If no error occurred, every other user is notified with the last 20 messages

    // Websockets active connexions.
    val openConnections =  mutable.Set.empty[cask.WsChannelActor]

    def latestMessages(): String =
        Layouts.msgList(msgSvc.getLatestMessages(MessageService.MESSAGES_LIMIT)).render

    def notifyNewMsg(channel: cask.WsChannelActor): Unit =
        channel.send(cask.Ws.Text(latestMessages()))

    @getSession(sessionSvc)
    @cask.postJson("/send")
    def send(msg: String)(session: Session) =
        if msg.isBlank then ujson.Obj("success" -> false, "err" -> "Message cannot be empty or blank !")
        else if session.getCurrentUser.isEmpty then  ujson.Obj("success" -> false, "err" -> "Please log in first !")
        else if msg.startsWith("@") then
            val mention = msg.substring(1, msg.indexOf(" "))
            if mention == "bot" then
                handleBot(msg, session)
            else
                sendMsg(session.getCurrentUser.get, msg, Some(mention))
        else sendMsg(session.getCurrentUser.get, msg)


    // TODO - Part 3 Step 4c: Process and store the new websocket connection made to `/subscribe`

    private def handleBot(msg: String, session: Session) = {
        try{
            val tokenized = tokenizerSvc.tokenize(msg.substring(4).trim.toLowerCase)
            val expr = Parser(tokenized).parsePhrases()
            val id = msgSvc.add(session.getCurrentUser.get, Layouts.msgContent(msg))
            openConnections.foreach(notifyNewMsg)
            sendMsg("bot", analyzerSvc.reply(session)(expr),None, Some(expr), Some(id))
        }catch {
            case _: Chat.UnexpectedTokenException => ujson.Obj("success" -> false, "err" -> "Invalid command!")
        }

    }

    private def sendMsg(user: String, msg: String, mention: Option[String] = None, exprType: Option[ExprTree] = None, replyToId: Option[Long] = None) = {
        msgSvc.add(user, Layouts.msgContent(msg), mention, exprType, replyToId)
        openConnections.foreach(notifyNewMsg)
        ujson.Obj("success" -> true, "err" -> "")
    }

//    private def sendMsg(msg: String, session: Session) = {
//        session.getCurrentUser.map(user => { //TODO Map for Optional[String]?
//            msgSvc.add(user, Layouts.msgContent(msg) /*TODO manage other params*/)
//            openConnections.foreach(notifyNewMsg)
//            ujson.Obj("success" -> true, "err" -> "")
//        }).getOrElse(ujson.Obj("success" -> false, "err" -> "Please log in first !"))
//    }

    @cask.websocket("/subscribe")
    def subscribe(): cask.WebsocketResult =
        cask.WsHandler { channel =>
            openConnections += channel
//            channel.send(cask.Ws.Text(latestMessages()))
            cask.WsActor {
//                case cask.Ws.Text("") => channel.send(cask.Ws.Close())
                case cask.Ws.Text(_) => notifyNewMsg(channel)
                case cask.Ws.Close(_, _) => openConnections -= channel
            }
        }


    // TODO - Part 3 Step 4d: Delete the message history when a GET is made to `/clearHistory`
    @getSession(sessionSvc) // This decorator fills the `(session: Session)` part of the `index` method.
    @cask.get("/clearHistory")
    def clearHistory()(session: Session) =
        msgSvc.deleteHistory()
        Layouts.homepage(session.getCurrentUser)


    // TODO - Part 3 Step 5: Modify the code of step 4b to process the messages sent to the bot (message
    //      starts with `@bot `). This message and its reply from the bot will be added to the message
    //      store together.
    //
    //      The exceptions raised by the `Parser` will be treated as an error (same as in step 4b)


    initialize()
end MessagesRoutes