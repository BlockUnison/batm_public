package org.knowm.xchange.alt.exceptions;

/** An exception indicating there the rate limit for making requests has been exceeded */
public class RateLimitExceededException extends ExchangeException {

  public RateLimitExceededException(String message) {
    super(message);
  }

  public RateLimitExceededException(Throwable e) {
    super(e);
  }

  public RateLimitExceededException(String message, Throwable cause) {
    super(message, cause);
  }

  public RateLimitExceededException() {
    super("Rate limit for making requests exceeded!");
  }
}
