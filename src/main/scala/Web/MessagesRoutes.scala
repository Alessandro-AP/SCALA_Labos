// SCALA - Labo 3
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 09.05.22

package Web

import Chat.{AnalyzerService, TokenizerService}
import Data.{AccountService, MessageService, Session, SessionService}

import scala.collection.mutable.ListBuffer
import castor.Context.Simple.global

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
        // TODO - Part 3 Step 2: Display the home page (with the message board and the form to send new messages)
        session.getCurrentUser.map(u => s"You are logged in as ${u} !")
          .getOrElse("You are not logged in !")
        Layouts.homepage(session.getCurrentUser, msgSvc)


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

    @getSession(sessionSvc)
    @cask.postJson("/send")
    def send(msg: String)(session: Session) =
        println(s"VALUE : $msg")
        ujson.Obj("success" -> true, "err" -> "")

    // TODO - Part 3 Step 4c: Process and store the new websocket connection made to `/subscribe`
    // Channels to every clients.
    val clients =  ListBuffer[cask.endpoints.WsChannelActor]()

    @cask.websocket("/subscribe")
    def subscribe(): cask.WebsocketResult =
        cask.WsHandler { channel =>
            clients += channel
            cask.WsActor {
                case cask.Ws.Text("") => channel.send(cask.Ws.Close())
                case cask.Ws.Text(data) =>
                    channel.send(cask.Ws.Text(data))
                case cask.Ws.Close(_, _) => clients -= channel
            }
        }

    // TODO - Part 3 Step 4d: Delete the message history when a GET is made to `/clearHistory`
    @getSession(sessionSvc) // This decorator fills the `(session: Session)` part of the `index` method.
    @cask.get("/clearHistory")
    def clearHistory()(session: Session) =
        msgSvc.deleteHistory()
        Layouts.homepage(session.getCurrentUser, msgSvc)

    //
    // TODO - Part 3 Step 5: Modify the code of step 4b to process the messages sent to the bot (message
    //      starts with `@bot `). This message and its reply from the bot will be added to the message
    //      store together.
    //
    //      The exceptions raised by the `Parser` will be treated as an error (same as in step 4b)
    


    initialize()
end MessagesRoutes
