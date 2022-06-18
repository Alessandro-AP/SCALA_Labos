// SCALA - Labo 4
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 19.06.22

package Services

import Chat.ExprTree
import Services.MessageService.{MsgContent, Username}
import scalatags.Text.Frag

class MessageConcurrentImpl(messageImpl: MessageImpl) extends MessageService:
  override def add(sender: Username, msg: MsgContent, mention: Option[Username] = None, exprType: Option[ExprTree] = None, replyToId: Option[Long] = None): Long =
    synchronized { messageImpl.add(sender, msg, mention, exprType, replyToId) }

  override def getLatestMessages(n: Int) =
    synchronized { messageImpl.getLatestMessages(n) }

  override def deleteHistory(): Unit =
    synchronized { messageImpl.deleteHistory() }
