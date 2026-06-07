package net.sorauta.screenartnet;

import jp.ncl.display.area.Area;
import jp.ncl.dmx.artnet.ArtnetSendDevice;
import jp.ncl.dmx.artnet.ArtnetSendDevicesLoader;
import net.sorauta.artnet.ArtNetSender;
import processing.core.PApplet;

/**
 * 「画面に描かれた pixels を ArtNet で実機 LED へ送る」薄いラッパ。演出・描画には依存しない。
 *
 * <p>デバイス XML（{@code devices_artnet.xml} と {@code device_*.xml}）が定義する LED 座標で
 * 画面 pixels をサンプルし、各 universe を unicast 送信する（実体は ncl/sorauta ライブラリ）。
 * 送信の直前に {@link ColorGrade} を画面へ適用できる（既定はその場適用＝プレビューと一致）。
 *
 * <h2>スレッド規約</h2>
 * 本クラスのメソッドは <b>Processing アニメーションスレッド（draw）からのみ</b>呼ぶこと。
 * Swing など別スレッドからは触らない。別スレッドが変えたい値（送信 ON/OFF・補正値）は
 * volatile な単純値で受け、draw 側で {@link #setSendEnabled(boolean)} /
 * {@link #setColorGrade(ColorGrade)} を呼んでから {@link #sendFromScreen(boolean)} する。
 */
public class ScreenArtnetSender {

  private final PApplet app;
  private final ArtNetSender sender;
  private final ArtnetSendDevice[] devices;
  private final Area displayArea;

  private boolean sendEnabled = true;
  private ColorGrade grade = ColorGrade.IDENTITY;

  /**
   * @param app          サンプル元の PApplet（画面サイズと pixels を提供）
   * @param devicesXmlPath {@code devices_artnet.xml} への絶対パス
   * @param deviceFolder  {@code device_*.xml} が置かれたフォルダ（末尾セパレータ付き）
   * @throws ArtnetSetupException デバイスのロードに失敗した場合
   */
  public ScreenArtnetSender(PApplet app, String devicesXmlPath, String deviceFolder) {
    if (app == null) {
      throw new ArtnetSetupException("PApplet must not be null");
    }
    this.app = app;
    this.sender = new ArtNetSender();
    this.displayArea = new Area();
    this.displayArea.setup(0, 0, app.width, app.height);

    try {
      // displayArea と plotArea は元実装と同じく同一インスタンスを渡す
      this.devices = ArtnetSendDevicesLoader.createAndSetupFromDevicesXmlFile(
          app, devicesXmlPath, sender, displayArea, displayArea, deviceFolder, "device_", 2);
    } catch (Exception e) {
      throw new ArtnetSetupException("failed to load devices from " + devicesXmlPath, e);
    }
    if (this.devices == null) {
      throw new ArtnetSetupException("device loader returned null for " + devicesXmlPath);
    }
  }

  /** 送信 ON/OFF。OFF でもサンプル（update）は行うのでプレビューは生きる。 */
  public void setSendEnabled(boolean enabled) {
    this.sendEnabled = enabled;
  }

  public boolean isSendEnabled() {
    return sendEnabled;
  }

  /** 送信前に画面 pixels へ適用する色補正。null は無補正として扱う。 */
  public void setColorGrade(ColorGrade grade) {
    this.grade = (grade != null) ? grade : ColorGrade.IDENTITY;
  }

  /** 読み込めたデバイス（= ArtNet universe）の数。 */
  public int deviceCount() {
    return devices.length;
  }

  /**
   * 1 フレーム分：色補正を適用 → 各デバイスが画面をサンプル → 送信。draw() の末尾で毎フレーム呼ぶ。
   *
   * @param gradeScreenInPlace true: 画面 pixels を補正後に書き戻す（WYSIWYG プレビュー・コピー無し）<br>
   *                           false: 送信用にコピーを補正し、画面は無加工のまま
   */
  public void sendFromScreen(boolean gradeScreenInPlace) {
    if (devices.length == 0) {
      return;
    }
    app.loadPixels();
    int[] sampleSrc = app.pixels;

    if (!grade.isIdentity()) {
      if (gradeScreenInPlace) {
        grade.applyInPlace(app.pixels);
        app.updatePixels();   // 画面（プレビュー）へ反映。pixels 配列はそのまま使える
        sampleSrc = app.pixels;
      } else {
        sampleSrc = grade.applyCopy(app.pixels);
      }
    }

    for (ArtnetSendDevice d : devices) {
      d.update(sampleSrc);
      if (sendEnabled) {
        d.send();
      }
    }
  }
}
