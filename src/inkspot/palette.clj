(ns inkspot.palette
  (:require [inkspot.color :as color])
  ^:clj
  (:import (java.awt Color Graphics2D GraphicsEnvironment RenderingHints)
           (java.awt.image BufferedImage)
           (java.io StringWriter)
           (org.apache.batik.dom GenericDOMImplementation)
           (org.apache.batik.svggen SVGGraphics2D)))

(defprotocol IGraphics2DTarget
  (create-context [this width height])
  (close [this]))

^:clj
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
(defn- draw-cell [^Graphics2D g2d x y w h color]
  (doto g2d
    (.setColor color)
    (.fillRect x y w h))
  g2d)

^:clj
(defn draw
  [color-swatch & {:keys [g2d-target cell-width cell-height cells-per-row border]}]
  (let [cell-width (or cell-width 10)
        cell-height (or cell-height cell-width)
        cells-per-row (or cells-per-row 48)
        num-cells (count color-swatch)
        width     (* cell-width cells-per-row)
        height    (* cell-height (Math/ceil (/ num-cells cells-per-row)))
        pos       (fn [i] [(* cell-width (mod i cells-per-row))
                           (* cell-height (quot i cells-per-row))])
        generator (->>
                   (iterate inc 0)
                   (map pos)
                   (map cons color-swatch))
        target    (g2d-target)
        g2d       (create-context target width height)]

    (doto g2d
      (.setBackground Color/WHITE)
      (.clearRect 0 0 width height))

    (let [w (- cell-width (or border 1))
          h (- cell-height (or border 1))]
      (doseq [[c x y] generator]
        (draw-cell g2d x y w h (color/coerce c))))

    (close target)))

^:clj
(defn bitmap []
  (let [img (atom nil)]
    (reify
      IGraphics2DTarget
      (create-context [this width height]
        (reset! img (create-image width height))
        (create-graphics @img))
      (close [this]
        @img))))

^:clj
(defn svg []
  (let [dom-impl (GenericDOMImplementation/getDOMImplementation)
        document (.createDocument dom-impl nil "svg" nil)
        svg-generator (SVGGraphics2D. document)]
    (reify
      IGraphics2DTarget
      (create-context [this width height]
        svg-generator)
      (close [this]
        (with-open [out (StringWriter.)]
          (.stream svg-generator out true)
          (.toString out))))))

