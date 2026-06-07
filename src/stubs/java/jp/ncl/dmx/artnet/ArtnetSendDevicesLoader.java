package jp.ncl.dmx.artnet;

import jp.ncl.display.area.Area;
import net.sorauta.artnet.ArtNetSender;
import processing.core.PApplet;

/**
 * コンパイル時のみのスタブ。実行時は nclLibraryForP5 が供給する。
 * シグネチャは元の {@code AwaodoriArtnet.pde} の呼び出しに合わせている（jar には含めない）。
 */
public class ArtnetSendDevicesLoader {
  public static ArtnetSendDevice[] createAndSetupFromDevicesXmlFile(
      PApplet app,
      String devicesXmlPath,
      ArtNetSender sender,
      Area displayArea,
      Area plotArea,
      String deviceFolder,
      String filePrefix,
      int arg) {
    return new ArtnetSendDevice[0];
  }
}
