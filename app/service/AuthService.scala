package service

import java.security.MessageDigest
import java.util.{Base64, UUID}

import model.User
import org.mindrot.jbcrypt.BCrypt
import play.api.cache.SyncCacheApi
import play.api.mvc.{Cookie, RequestHeader}

import scala.concurrent.duration.DurationDouble
import scalikejdbc._

class AuthService(cacheApi: SyncCacheApi) {

  val mda = MessageDigest.getInstance("SHA-512")
  val cookieHeader = "X-Auth-Token"

  /** Maybe login given user with given password */
  def login(userCode: String, password: String): Option[Cookie] = {
    for {
      user <- checkUser(userCode, password)
    } yield createCookie(user)
  }

  /** Maybe get the current user from cache */
  def checkCookie(header: RequestHeader): Option[User] = {
    for {
      cookie <- header.cookies.get(cookieHeader)
      user <- cacheApi.get[User](cookie.value)
    } yield user
  }

  private def checkUser(userCode: String, password: String): Option[User] = {
    DB.readOnly {
      implicit session =>
        val maybeUser = sql"SELECT * FROM users WHERE user_code = $userCode".map(User.fromRS).single().apply()
        maybeUser.flatMap {
          user =>
            if(BCrypt.checkpw(password, user.password)) {
              Some(user)
            } else {
              None
            }
        }
    }
  }

  private def createCookie(user: User): Cookie = {
    val randomPart = UUID.randomUUID().toString.toUpperCase
    val userPart = user.userId.toString.toUpperCase
    val key = s"$randomPart|$userPart"
    val token = Base64.getEncoder.encodeToString(mda.digest(key.getBytes))
    val duration = 10.hours
    cacheApi.set(token, duration)
    Cookie(cookieHeader, token, maxAge = Some(duration.toSeconds.toInt))
  }


}
