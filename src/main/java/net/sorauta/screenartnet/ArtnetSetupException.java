package net.sorauta.screenartnet;

/**
 * {@link ScreenArtnetSender} の初期化失敗（デバイス XML 不在・ロード失敗など）。
 *
 * <p>非チェック例外にしているのは、Processing スケッチ（PDE）で try/catch を強制すると
 * 取り回しが悪いため。呼び出し側は必要に応じて捕捉する。
 */
public class ArtnetSetupException extends RuntimeException {

  public ArtnetSetupException(String message) {
    super(message);
  }

  public ArtnetSetupException(String message, Throwable cause) {
    super(message, cause);
  }
}
