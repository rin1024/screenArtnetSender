# libs/

ここに置く jar は **任意**（コンパイルはスタブで通るため不要）。

本物のクラスでビルド・動作確認したい場合のみ、ホスト（Processing）環境から以下を置く:

- Processing の `core.jar`
- nclLibraryForP5 の jar（`jp.ncl.dmx.artnet` / `jp.ncl.display.area` を含む）
- sorauta artnet の jar（`net.sorauta.artnet.ArtNetSender`）

ここに置いた `*.jar` は `compileOnly` に乗る（生成 jar には含まれない）。`*.jar` は `.gitignore` 済み。
