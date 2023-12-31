package org.authenticator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import static java.nio.charset.StandardCharsets.*;

public class Utils {

  private Utils() {
    throw new IllegalStateException("Utility class");
  }

  public static String generateSecretKey() {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[20];
    random.nextBytes(bytes);
    Base32 base32 = new Base32();
    return base32.encodeToString(bytes);
  }

  public static String getTOTPCode(String secretKey) {
    Base32 base32 = new Base32();
    byte[] bytes = base32.decode(secretKey);
    String hexKey = Hex.encodeHexString(bytes);
    return TOTP.getOTP(hexKey);
  }

  public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
    return "otpauth://totp/"
      + URLEncoder.encode(issuer + ":" + account, UTF_8).replace("+", "%20")
      + "?secret=" + URLEncoder.encode(secretKey, UTF_8).replace("+", "%20")
      + "&issuer=" + URLEncoder.encode(issuer, UTF_8).replace("+", "%20");
  }

  public static void createQRCode(String barCodeData, String filePath, int height, int width)
    throws WriterException, IOException {
    BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
    try (FileOutputStream out = new FileOutputStream(filePath)) {
      MatrixToImageWriter.writeToStream(matrix, "png", out);
    }
  }

}
