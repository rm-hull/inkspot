(ns inkspot.color-chart
  (:require
    [inkspot.spectrum :as spectrum]
    [inkspot.color :as color]
    [inkspot.converter :as conv]))

(def web-safe-colors
  "A swatch of web safe colours"
  [
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

(defn spectrum
  "Yields a spectral range from Red (420 THz) to Indigo (750 THz) with
   the given number of colours in between."
  [steps]
  (let [f1 420 ; Red = 420 THz
        f2 750 ; Indigo = 750 THz
        step (double (/ (- f2 f1) steps))]
    (->>
      (iterate (partial + step) f1)
      (map (comp color/coerce color/gamma spectrum/frequency-color))
      (take steps)
      (vec))))

(defn rainbow
  "Yields rainbow colors, although not entirely certain the formulas
   are correct; cannot remember where this came from but dates to early
   2000's..."
  [steps]
  (letfn [(c [idx]
            { :red   1.0
              :green (Math/sin (/ (* 3 idx) steps))
              :blue  (double (/ idx steps)) })]
    (->>
      (range steps)
      (mapv (comp color/coerce color/gamma c)))))

(defn- xrange [start end num-steps]
  (let [diff (- end start)
        step (/ diff num-steps)]
    (if (zero? diff)
      (repeat num-steps start)
      (range start end step))))

(defn gradient
  "Linear gradient between two colours, with the given number of
   graduations."
  [from-color to-color steps]
  (let [a (color/coerce from-color)
        b (color/coerce to-color)
        reds   (xrange (color/red a) (color/red b) steps)
        greens (xrange (color/green a) (color/green b) steps)
        blues  (xrange (color/blue a) (color/blue b) steps)
        alphas (xrange (color/alpha a) (color/alpha b) steps)]
    (map (comp color/coerce vector) reds greens blues alphas)))

(defn heatmap
  "Blackbody radiation (black, through red, orange, yellow to white),
   with the given number of graduations."
  [steps]
  (->>
    [:black :red :orange :yellow :white]
    (partition 2 1)
    (map (comp #(conj % (quot steps 4)) vec))
    (mapcat (partial apply gradient))))

(defn cube-helix
  "Unlike most other color schemes cubehelix was designed by D.A. Green to be
   monotonically increasing in terms of perceived brightness. Also, when
   printed on a black and white postscript printer, the scheme results in a
   greyscale with monotonically increasing brightness. This color scheme is
   named cubehelix because the r,g,b values produced can be visualised as a
   squashed helix around the diagonal in the r,g,b color cube.

   For a unit color cube (i.e. 3-D coordinates for r,g,b each in the range 0 to
   1) the color scheme starts at (r,g,b) = (0,0,0), i.e. black, and finishes at
   (r,g,b) = (1,1,1), i.e. white. For some fraction *x*, between 0 and 1, the
   color is the corresponding grey value at that fraction along the black to
   white diagonal (x,x,x) plus a color element. This color element is
   calculated in a plane of constant perceived intensity and controlled by the
   following parameters.

   Optional keywords:

       gamma         gamma factor to emphasise either low intensity values
                     (gamma < 1), or high intensity values (gamma > 1);
                     defaults to 1.0.

       start-color   the start color; defaults to 0.5 (i.e. purple).

       rotations     the number of r,g,b rotations in color that are made
                     from the start to the end of the color scheme; defaults
                     to -1.5 (i.e. -> B -> G -> R -> B).

       hue           the hue parameter which controls how saturated the
                     colors are. If this parameter is zero then the color
                     scheme is purely a greyscale; defaults to 1.0.

   Derived from: https://github.com/matplotlib/matplotlib/blob/master/lib/matplotlib/_cm.py#L59"

  [steps & {:keys [gamma start-color rotations hue]}]
  (let [gamma (or gamma 1.0)
        start (or start-color 0.5)
        hue   (or hue 1.0)
        rot   (or rotations -1.5)
        color (fn [p0 p1]
                (fn [x]
                  (let [xg (Math/pow x gamma)
                        a  (* hue xg (- 1 xg) 0.5)
                        phi (* 2 Math/PI (+ (/ start 3) (* rot x)))]
                    (+ xg (* a (+ (* p0 (Math/cos phi)) (* p1 (Math/sin phi))))))))
        red   (color -0.14861 1.78277)
        green (color -0.29227 -0.90649)
        blue  (color 1.97294, 0.0)
        rgb   (fn [x]
                (color/coerce
                  (mapv (partial * 255) [(red x) (green x) (blue x)])))]

    (mapv rgb (xrange 0 1 steps))))
