package processing.core;

/**
 * コンパイル時のみのスタブ。実行時は Processing の本物の core.jar が供給する。
 * 本ライブラリが使う最小限のメンバだけを宣言している（jar には含めない）。
 */
public class PApplet {
  public int[] pixels;
  public int width;
  public int height;

  public void loadPixels() {}
  public void updatePixels() {}
}
