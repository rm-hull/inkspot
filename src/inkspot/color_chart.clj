(ns inkspot.color-chart
  (require [inkspot.spectrum :as spectrum]
           [inkspot.color :as color])
  ^:clj
  (:import [java.awt.image BufferedImage]
           [java.awt.geom AffineTransform GeneralPath Ellipse2D$Double]
           [java.awt Color Graphics2D RenderingHints BasicStroke GraphicsEnvironment]))

(def web-safe-colors [
  "#990033" "#FF3366" "#CC0033" "#FF0033" "#FF9999" "#CC3366" "#FFCCFF" "#CC6698"
  "#993366" "#660033" "#CC3399" "#FF99CC" "#FF66CC" "#FF99FF" "#FF6699" "#CC0066"
  "#FF0066" "#FF3399" "#FF0099" "#FF33CC" "#FF00CC" "#FF66FF" "#FF33FF" "#FF00FF"
  "#CC0099" "#990066" "#CC66CC" "#CC33CC" "#CC99FF" "#CC66FF" "#CC33FF" "#993399"
  "#CC00CC" "#CC00FF" "#9900CC" "#990099" "#CC99CC" "#996699" "#663366" "#660099"
  "#9933CC" "#660066" "#9900FF" "#9933FF" "#9966CC" "#330033" "#663399" "#6633CC"
  "#6600CC" "#9966FF" "#330066" "#6600FF" "#6633FF" "#CCCCFF" "#9999FF" "#9999CC"
  "#6666CC" "#6666FF" "#666699" "#333366" "#333399" "#330099" "#3300CC" "#3300FF"
  "#3333FF" "#3333CC" "#0066FF" "#0033FF" "#3366FF" "#3366CC" "#000066" "#000033"
  "#0000FF" "#000099" "#0033CC" "#0000CC" "#336699" "#0066CC" "#99CCFF" "#6699FF"
  "#003366" "#6699CC" "#006699" "#3399CC" "#0099CC" "#66CCFF" "#3399FF" "#003399"
  "#0099FF" "#33CCFF" "#00CCFF" "#99FFFF" "#66FFFF" "#33FFFF" "#00FFFF" "#00CCCC"
  "#009999" "#669999" "#99CCCC" "#CCFFFF" "#33CCCC" "#66CCCC" "#339999" "#336666"
  "#006666" "#003333" "#00FFCC" "#33FFCC" "#33CC99" "#00CC99" "#66FFCC" "#99FFCC"
  "#00FF99" "#339966" "#006633" "#336633" "#669966" "#66CC66" "#99FF99" "#66FF66"
  "#339933" "#99CC99" "#66FF99" "#33FF99" "#33CC66" "#00CC66" "#66CC99" "#009966"
  "#009933" "#33FF66" "#00FF66" "#CCFFCC" "#CCFF99" "#99FF66" "#99FF33" "#00FF33"
  "#33FF33" "#00CC33" "#33CC33" "#66FF33" "#00FF00" "#66CC33" "#006600" "#003300"
  "#009900" "#33FF00" "#66FF00" "#99FF00" "#66CC00" "#00CC00" "#33CC00" "#339900"
  "#99CC66" "#669933" "#99CC33" "#336600" "#669900" "#99CC00" "#CCFF66" "#CCFF33"
  "#CCFF00" "#999900" "#CCCC00" "#CCCC33" "#333300" "#666600" "#999933" "#CCCC66"
  "#666633" "#999966" "#CCCC99" "#FFFFCC" "#FFFF99" "#FFFF66" "#FFFF33" "#FFFF00"
  "#FFCC00" "#FFCC66" "#FFCC33" "#CC9933" "#996600" "#CC9900" "#FF9900" "#CC6600"
  "#993300" "#CC6633" "#663300" "#FF9966" "#FF6633" "#FF9933" "#FF6600" "#CC3300"
  "#996633" "#330000" "#663333" "#996666" "#CC9999" "#993333" "#CC6666" "#FFCCCC"
  "#FF3333" "#CC3333" "#FF6666" "#660000" "#990000" "#CC0000" "#FF0000" "#FF3300"
  "#CC9966" "#FFCC99" "#FFFFFF" "#CCCCCC" "#999999" "#666666" "#333333" "#000000" ])

(defn color-seq
  "Take at most n colors from the given color swatch, cycle through them
   repeatedly and lazily, starting from the start offset, stepping over
   step in each yield."
  [colors n start step]
  (->>
    (cycle colors)
    (drop start)
    (take-nth step)
    (take n)
    (cycle)))

(defn color-mapper
  "Return a function which accepts a single numeric argument in the range
   low (inclusive) to high (exclusive): the generated function will return
   a color from the given swatch which approximately maps (in a linear sense)
   argument in the low..high range.

   Do not use with infinite sequences."
  [colors low high]
  (let [g (/ (- high low) (count colors))
        v (vec colors)]
    (fn [n]
      (get v (int (/ (- n low) g))))))

(defn spectrum [num-colors]
  (let [f1 420 ; Red = 420 THz
        f2 750 ; Indigo = 750 THz
        step (double (/ (- f2 f1) num-colors))]
    (->>
      (iterate (partial + step) f1)
      (map (comp color/coerce color/gamma spectrum/frequency-color))
      (take num-colors)
      (vec))))

(defn rainbow [num-colors]
  (letfn [(c [idx]
            { :red   1.0
              :green (Math/sin (/ (* 3 idx) num-colors))
              :blue  (double (/ idx num-colors)) })]
    (->>
      (range num-colors)
      (mapv (comp color/coerce color/gamma c)))))

(defn- ^BufferedImage create-image [w h]
  (if (GraphicsEnvironment/isHeadless)
    (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)
    (.createCompatibleImage
       (.getDefaultConfiguration
         (.getDefaultScreenDevice
           (GraphicsEnvironment/getLocalGraphicsEnvironment)))
       w h)))

^:clj
(defn- ^Graphics2D create-graphics [^BufferedImage img]
  (let [g2d (.createGraphics img)]
    (doto g2d
      (.setRenderingHint RenderingHints/KEY_STROKE_CONTROL RenderingHints/VALUE_STROKE_NORMALIZE)
      (.setRenderingHint RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON)
      (.setRenderingHint RenderingHints/KEY_RENDERING RenderingHints/VALUE_RENDER_QUALITY))
    g2d))

^:clj
(defn draw-cell [^Graphics2D g2d x y size color]
  (doto g2d
    (.setColor color)
    (.fillRect x y size size))
  g2d)

^:clj
(defn create-palette
  [color-swatch & {:keys [cell-size cells-per-row]
                   :or   {cell-size 10 cells-per-row 48}}]
  (let [num-cells (count color-swatch)
        width     (* cell-size cells-per-row)
        height    (* cell-size (Math/ceil (/ num-cells cells-per-row)))
        pos       (fn [i] [(* cell-size (mod i cells-per-row))
                           (* cell-size (quot i cells-per-row))])
        generator (->>
                    (iterate inc 0)
                    (map pos)
                    (map cons color-swatch))
        img       (create-image width height)
        g2d       (create-graphics img)]

    (doto g2d
      (.setBackground Color/WHITE)
      (.clearRect 0 0 width height))

    (doseq [[color x y] generator]
      (draw-cell g2d x y (dec cell-size) (color/coerce color)))

    (.dispose g2d)
    img))