(ns inkspot.color
  (:require [clojure.string :as string])
  (:use [inkspot.common :only [parse-int parse-double]])
  ^:clj
  (:import [java.awt Color]))

(defprotocol IColor
  (red [c])
  (green [c])
  (blue [c])
  (alpha [c])
  (coerce [c]))

(defn rgba
  "Convert an IColor to rgba(...) format"
  [color]
  (str
    "rgba("
    (red   color) ","
    (green color) ","
    (blue  color) ","
    (alpha color) ")"))

(defn to-color
  "Converts an IColor to a native color representation.
   rgb values should be an integer in the 0-255 range,
   whilst alpha channel is a double in the range 0.0 - 1.0"
  [color]
  ^{:cljs '(rgba color)}
  (Color.
    (int (red color))
    (int (green color))
    (int (blue color))
    (int (* (alpha color) 255))))

(defn rgb
  "Construct a tuple of RGB (or RGBA) elements from xs, expected values
   (numeric or string) in range 0-255."
  [xs]
  (map (comp parse-int string/trim) xs))

(defn gamma
  "Construct a tuple of RGB elements from red/green/blue components,
   expected values (double) in range 0.0-1.0"
  [{:keys [red green blue scale]
    :or   {red 0.0 green 0.0 blue 0.0 scale 1.0}}]
  [(int (* 255 red scale))
   (int (* 255 green scale))
   (int (* 255 blue scale))])

(defn- color-vec [[_ & xs]]
  (vec
    (case (count xs)
      1 (->> xs first seq (partition 2) (map (comp #(parse-int % 16) (partial apply str))) vec)
      3 (rgb xs)
      4 (concat (rgb (take 3 xs))
          [(-> (last xs) string/trim parse-double)]))))

(defn int->color
  "Construct a tuple of RGB elements from an integer."
  [n]
  (loop [ret nil
         n n]
    (if (= (count ret) 3)
      (vec ret)
      (recur
        (cons (mod n 256) ret)
        (quot n 256)))))

(defn string->color [s]
  (condp re-matches s
    #"#(.*)" :>> color-vec
    #"rgb\((.*),(.*),(.*)\)" :>> color-vec
    #"rgba\((.*),(.*),(.*),(.*)\)" :>> color-vec))

(extend-type ^{:cljs cljs.core.PersistentVector} clojure.lang.PersistentVector
  IColor
  (red    [[r _ _ _]] r)
  (green  [[_ g _ _]] g)
  (blue   [[_ _ b _]] b)
  (alpha  [[_ _ _ a]] (or a 1.0))
  (coerce [arr] (to-color arr)))

(extend-type java.lang.Long
  IColor
  (red    [n] (red (int->color n)))
  (green  [n] (green (int->color n)))
  (blue   [n] (blue (int->color n)))
  (alpha  [n] (alpha (int->color n)))
  (coerce [n] (to-color n)))

(extend-type java.lang.String
  IColor
  (red    [s] (red (string->color s)))
  (green  [s] (green (string->color s)))
  (blue   [s] (blue (string->color s)))
  (alpha  [s] (alpha (string->color s)))
  (coerce [s] (to-color s)))

^:clj
(extend-type java.awt.Color
  IColor
  (red    [c] (.getRed c))
  (green  [c] (.getGreen c))
  (blue   [c] (.getBlue c))
  (alpha  [c] (/ (.getAlpha c) 255.0))
  (coerce [c] c))

#_({:cljs
(extend-type array
  IColor
  (red   [[r _ _ _]] r)
  (green [[_ g _ _]] g)
  (blue  [[_ _ b _]] b)
  (alpha [[_ _ _ a]] a))})

(defn adjust-color [style & [color]]
  (let [color (or color "rgb(255,255,255)")
        alpha (style {:transparent 0.0 :translucent 0.6 :opaque 1.0 :shaded 1.0})]
    (when alpha
      (to-color [(red color) (green color) (blue color) alpha]))))

(defn scale [color weight] ; is this the same as brightness?
  [ (* weight (red color))
    (* weight (green color))
    (* weight (blue color))
    (alpha color)])

(defn mix
  "Mix the colors in RGB space in the proportions given, else if
   none given, in equal measure."
  ([colors]
    (mix colors (repeat (count colors) 1)))
  ([colors proportions]
    (let [cnt (reduce + proportions)
          sum (->>
                (map scale colors proportions)
                (reduce (partial map +)))]
      (coerce (mapv #(int (/ % cnt)) sum)))))
