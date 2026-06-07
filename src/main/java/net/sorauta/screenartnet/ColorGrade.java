package net.sorauta.screenartnet;

/**
 * 明るさ・コントラスト・彩度・R/G/B ゲインの色補正。値はイミュータブルで、生成後に変化しない。
 *
 * <p>毎フレーム生成して {@link ScreenArtnetSender#setColorGrade(ColorGrade)} に渡すことで、
 * Swing 等の別スレッドが補正値を書き換えても 1 フレーム内の一貫性が保たれる
 * （送信処理の最中に値が変わってティアリングすることがない）。
 *
 * <p>色は Processing と同じ 0xAARRGGBB の int として扱う。本クラスは Processing に依存しない。
 *
 * <p>適用順: 彩度 → コントラスト → 明るさ × 各チャンネルゲイン → 0..255 へクランプ。
 * 既存の bri/con/sat に加え、R/G/B ゲインで全体の色味（ホワイトバランス等）を独立に調整できる。
 */
public final class ColorGrade {

  /** 無補正（全係数 1.0）。 */
  public static final ColorGrade IDENTITY = new ColorGrade(1f, 1f, 1f);

  public final float brightness;
  public final float contrast;
  public final float saturation;
  public final float rGain;
  public final float gGain;
  public final float bGain;

  /** R/G/B ゲインは 1.0（無調整）で構築する利便コンストラクタ。 */
  public ColorGrade(float brightness, float contrast, float saturation) {
    this(brightness, contrast, saturation, 1f, 1f, 1f);
  }

  public ColorGrade(float brightness, float contrast, float saturation,
                    float rGain, float gGain, float bGain) {
    this.brightness = brightness;
    this.contrast = contrast;
    this.saturation = saturation;
    this.rGain = rGain;
    this.gGain = gGain;
    this.bGain = bGain;
  }

  /** 無補正（全係数 1.0）かどうか。true のときは適用処理を丸ごと省ける。 */
  public boolean isIdentity() {
    return brightness == 1f && contrast == 1f && saturation == 1f
      && rGain == 1f && gGain == 1f && bGain == 1f;
  }

  /** 1 ピクセル（0xAARRGGBB）に補正を適用して返す。アルファは保持する。 */
  public int apply(int argb) {
    int a = (argb >>> 24) & 0xFF;
    float r = (argb >> 16) & 0xFF;
    float g = (argb >> 8) & 0xFF;
    float b = argb & 0xFF;

    float gray = 0.299f * r + 0.587f * g + 0.114f * b;
    r = gray + (r - gray) * saturation;
    g = gray + (g - gray) * saturation;
    b = gray + (b - gray) * saturation;

    int ri = gradeChannel(r, rGain);
    int gi = gradeChannel(g, gGain);
    int bi = gradeChannel(b, bGain);
    return (a << 24) | (ri << 16) | (gi << 8) | bi;
  }

  /** pixels 配列をその場で補正する（コピーを作らない）。 */
  public void applyInPlace(int[] pixels) {
    if (isIdentity()) {
      return;
    }
    for (int i = 0; i < pixels.length; i++) {
      pixels[i] = apply(pixels[i]);
    }
  }

  /** 元配列を変えずに、補正済みの新しい配列を返す。 */
  public int[] applyCopy(int[] pixels) {
    int[] out = new int[pixels.length];
    if (isIdentity()) {
      System.arraycopy(pixels, 0, out, 0, pixels.length);
      return out;
    }
    for (int i = 0; i < pixels.length; i++) {
      out[i] = apply(pixels[i]);
    }
    return out;
  }

  /**
   * 1 チャンネル（0..255 想定の float）にコントラスト→明るさ→チャンネルゲインを適用し
   * 0..255 へ丸める。
   */
  private int gradeChannel(float ch, float gain) {
    float v = (ch / 255f - 0.5f) * contrast + 0.5f;
    v *= brightness * gain;
    int o = Math.round(v * 255f);
    return o < 0 ? 0 : (o > 255 ? 255 : o);
  }

  @Override
  public String toString() {
    return "ColorGrade[bri=" + brightness + ", con=" + contrast + ", sat=" + saturation
      + ", r=" + rGain + ", g=" + gGain + ", b=" + bGain + "]";
  }
}
