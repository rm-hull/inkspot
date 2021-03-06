(defproject rm-hull/inkspot "0.3.0"
  :description "A small Clojure/ClojureScript library for creating colour swatches"
  :url "https://github.com/rm-hull/inkspot"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.9.0"]
    [org.clojure/clojurescript "1.10.339"]
    [org.clojure/data.json "0.2.6"]
    [org.apache.xmlgraphics/batik-dom "1.10"]
    [org.apache.xmlgraphics/batik-gvt "1.10"]
    [org.apache.xmlgraphics/batik-svggen "1.10"]
    [rm-hull/cljs-test "0.0.8-SNAPSHOT"]]
  :scm {:url "git@github.com:rm-hull/inkspot.git"}
  :plugins [
    [lein-codox "0.10.5"]
    [lein-cljsbuild "1.1.7"]
    [lein-cljfmt "0.6.1"]]
  :hooks [leiningen.cljsbuild]
  :source-paths ["src"]
  :jar-exclusions [#"(?:^|/).git"]
  :cljsbuild {
    :repl-listen-port 9000
    :repl-launch-commands
      {"firefox" ["firefox"]
       "firefox-demo" ["firefox" "doc/gallery/cljs-demo/gallery.html"]}
    :test-commands {"phantomjs" ["phantomjs" "target/unit-test.js"]}
    :builds {
      :main {
        :source-paths ["src"]
        :jar true
        :compiler {
          :output-to "target/inkspot.js"
          :source-map "target/inkspot.map"
          :static-fns true
          :optimizations :advanced
          :pretty-print false }}
      :test {
        :source-paths ["src" "test"]
        :incremental? true
        :compiler {
          :output-to "target/unit-test.js"
          :source-map "target/unit-test.map"
          :static-fns true
          :optimizations :whitespace
          :pretty-print true }}}}
  :codox {
    :source-paths ["src"]
    :doc-files [
      "doc/basic-usage.md"
      "doc/color-swatches.md"
      "doc/todo.md"
      "doc/references.md"
      "LICENSE.md"
    ]
    :output-path "doc/api"
    :source-uri "http://github.com/rm-hull/inkspot{filepath}#L{line}" }
  :min-lein-version "2.8.1"
  :profiles {
    :dev {
      :global-vars {*warn-on-reflection* true}
      :dependencies [
        [rm-hull/cljs-test "0.0.8-SNAPSHOT"]]
      :plugins [
        [lein-cloverage "1.0.13"]]}})
