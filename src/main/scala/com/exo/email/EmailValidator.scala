
package com.exo.email

import javax.mail.internet.InternetAddress
import javax.mail.internet.AddressException

trait EmailValidator {

  def validate(email: String) = {
    org.apache.commons.validator.EmailValidator.getInstance().isValid(email)
  }
}