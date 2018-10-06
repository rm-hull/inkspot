(ns inkspot.converter
  "Converters between RGB space and other colour spaces, inspired
   from python implementations from https://github.com/xav/Grapefruit"
  (:require [inkspot.color :refer [coerce red green blue alpha]]))

(defn rgb->hsv
  "RGB to HSV (Hue, Saturation, Value) conversion."
  [color]
  (let [r (/ (red color) 255.0)
        g (/ (green color) 255.0)
        b (/ (blue color) 255.0)
        a (alpha color)
        v (max r g b)
        d (- v (min r g b))]
    (if (zero? d)
      [0.0 0.0 v]
      (let [s (/ d v)
            [dr dg db] (map #(/ (- v %) d) [r g b])
            h (condp = v
                r (- db dg)          ; between yellow & magenta
                g (- (+ 2.0 dr) db)  ; between cyan & yellow
                b (- (+ 4.0 dg) dr)) ; between magenta & cyan
            h (mod (* h 60.0) 360.0)]
        [h s v a]))))

(defn hsv->rgb
  "HSV (Hue, Saturation, Value) to RGB conversion."
  [[h s v a]]
  (if (zero? s)
    (coerce [v v v])
    (let [h (mod (/ h 60) 6)
          i (int h)
          f (if (even? i)
              (- 1 (- h i))
              (- h i))
          v (* v 255.0)
          m (* v (- 1.0 s))
          n (* v (- 1.0 (* s f)))
          a (or a 1.0)]
      (coerce
       (condp = i
         0 [v n m a]
         1 [n v m a]
         2 [m v n a]
         3 [m n v a]
         4 [n m v a]
         [v m n a])))))

(defn rgb->hsl
  "RGB to HSL (Hue, Saturation and Luminosity) conversion."
  [color]
  (let [r (/ (red color) 255.0)
        g (/ (green color) 255.0)
        b (/ (blue color) 255.0)
        a (alpha color)
        min-val (min r g b)
        max-val (max r g b)
        l (/ (+ max-val min-val) 2.0)]
    (if (= min-val max-val)
      [0.0 0.0 l]   ; achromatic (gray)
      (let [d (- max-val min-val)
            s (if (< l 0.5)
                (/ d (+ max-val min-val))
                (/ d (- 2.0 max-val min-val)))
            [dr dg db] (mapv #(/ (- max-val %) d) [r g b])
            h (condp = max-val
                r (- db dg)
                g (- (+ 2.0 dr) db)
                b (- (+ 4.0 dg) dr))
            h (mod (* h 60.0) 360.0)]
        [h s l a]))))

(defn hsl->rgb
  "HSL (Hue, Saturation and Luminosity) to RGB conversion."
  [[h s l a]]
  (if (zero? s)
    (coerce [l l l])   ; achromatic (gray)
    (let [n2 (if (< l 0.5)
               (* l (+ 1.0 s))
               (- (+ l s) (* l s)))
          n1 (- (* 2.0 l) n2)
          h (/ h 60.0)
          f (fn [h] (let [h (mod h 6.0)]
                      (cond
                        (< h 1.0) (+ n1 (* (- n2 n1) h))
                        (< h 3.0) n2
                        (< h 4.0) (+ n1 (* (- n2 n1) (- 4.0 h)))
                        :else     n1)))
          r (* 255.0 (f (+ h 2)))
          g (* 255.0 (f h))
          b (* 255.0 (f (- h 2)))]
      (coerce [r g b (or a 1.0)]))))

(defn rgb->yuv
  "RGB to Y'UV (Luma, Chrominance) Conversion."
  [color]
  (let [r (/ (red color) 255.0)
        g (/ (green color) 255.0)
        b (/ (blue color) 255.0)
        a (alpha color)
        y (+ (* r  0.29900) (* g  0.58700) (* b  0.11400))
        u (+ (* r -0.14713) (* g -0.28886) (* b  0.43600))
        v (+ (* r  0.61500) (* g -0.51499) (* b -0.10001))]
    [y u v a]))

(defn yuv->rgb [[y u v a]]
  "Y'UV (Luma, Chrominance) to RGB Conversion."
  (let [r (* (+ y (* v 1.13983)) 255.0)
        g (* (- y (* u 0.39465) (* v 0.58060)) 255.0)
        b (* (+ y (* u 2.03211)) 255.0)]
    (coerce [r g b (or a 1.0)])))

(defn grayscale
  "RGB to greyscale conversion, largely by taking the Luma
   value from Y'UV conversion."
  [color]
  (let [[y _ _ a] (map (partial * 255.0) (rgb->yuv color))]
    (coerce [y y y a])))

(defn complementary
  "Complimentary color conversion, largely by taking the HSL
   value, and rotating the Hue by 180 degrees, and then converting
   back to RGB."
  [color]
  (let [[h s l a] (rgb->hsl color)]
    (hsl->rgb [(mod (+ h 180) 360) s l a])))