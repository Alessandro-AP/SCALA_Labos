import Chat.*
import Services.*
import Utils.*
import Web.{StaticRoutes, UsersRoutes}
import Web.{MessagesRoutes}
import java.util.concurrent.TransferQueue
import java.util.concurrent.LinkedTransferQueue

object MainFuture extends cask.Main:
  val tq: TransferQueue[String] = new LinkedTransferQueue[String]()

  val spellCheckerSvc = new SpellCheckerImpl(Dictionary.dictionary)
  val tokenizerSvc = new TokenizerService(spellCheckerSvc)
  val sessionSvc = new SessionImpl()
  val productSvc = new ProductImpl()
  val accountSvc: AccountService = new AccountImpl()
  val analyzerSvc = new AnalyzerService(productSvc, accountSvc, tq)
  val msgSvc: MessageService = new MessageConcurrentImpl(new MessageImpl())

  val allRoutes = Seq(
      StaticRoutes(),
      UsersRoutes(accountSvc, sessionSvc),
      MessagesRoutes(tokenizerSvc, analyzerSvc, msgSvc, accountSvc, sessionSvc, tq),
  )

  override def port: Int = 8980
