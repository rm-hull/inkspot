(ns inkspot.examples
  (require [clojure.java.io :as io]
           [inkspot.color :as color]
           [inkspot.color-chart :as cc]
           [inkspot.palette :as palette]
           [inkspot.color-chart.lindsay :as lindsay]
           [inkspot.color-chart.x11 :as x11])
  (import [javax.imageio ImageIO]))

;; Distinct Color Swatches
(doseq [[k v] {:web-safe-colors (map color/coerce cc/web-safe-colors)
               :lindsay         (map color/coerce (vals lindsay/swatch))
               :x11             (map color/coerce (vals x11/swatch))}
        :let [f (io/file (str "example/palette/" (name k) ".png"))]]
  (ImageIO/write (palette/draw v :g2d-target palette/bitmap) "png" f))

;; Interpolated Color Swatches
(doseq [[k v] {:spectrum   (cc/spectrum 216)
               :rainbow    (cc/rainbow 216)
               :gradient1  (cc/gradient :orange :blue 216)
               :gradient2  (cc/gradient :red :snow 216)
               :heatmap    (cc/heatmap 216)
               :cube-helix (cc/cube-helix 216)}
        :let [f (io/file (str "example/palette/" (name k) ".png"))]]
  (ImageIO/write (palette/draw v :g2d-target palette/bitmap
                                 :cell-width 2 :cell-height 50
                                 :cells-per-row 216 :border 0) "png" f))
