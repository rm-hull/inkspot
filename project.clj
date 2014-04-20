(defproject rm-hull/inkspot "0.0.1-SNAPSHOT"
  :description "A small Clojure/ClojureScript library for creating colour swatches"
  :url "https://github.com/rm-hull/inkspot"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.6.0"]
    [org.clojure/clojurescript "0.0-2202"]
    [rm-hull/cljs-test "0.0.8-SNAPSHOT"]]
  :scm {:url "git@github.com:rm-hull/inkspot.git"}
  :plugins [
    [codox "0.6.7"]
    [lein-cljsbuild "1.0.3"]
    [com.birdseye-sw/lein-dalap "0.1.0"]]
  :hooks [
    leiningen.dalap
    leiningen.cljsbuild]
  :source-paths ["src"]
  :cljsbuild {
    :repl-listen-port 9000
    :repl-launch-commands
      {"firefox" ["firefox"]
       "firefox-demo" ["firefox" "doc/gallery/cljs-demo/gallery.html"]}
    :test-commands  {"phantomjs"  ["phantomjs" "target/unit-test.js"]}
    :builds {
      :main {
        :source-paths ["target/generated-src"]
        :jar true
        :compiler {
          :output-to "target/inkspot.js"
          :source-map "target/inkspot.map"
          :static-fns true
          ;:optimizations :advanced
          :pretty-print true }}
      :test {
        :source-paths ["target/generated-src" "test"]
        :incremental? true
        :compiler {
          :output-to "target/unit-test.js"
          :source-map "target/unit-test.map"
          :static-fns true
          :optimizations :whitespace
          :pretty-print true }}}}
  :codox {
    :sources ["src"]
    :output-dir "doc/api"
    :src-dir-uri "http://github.com/rm-hull/inkspot/blob/master/"
    :src-linenum-anchor-prefix "L" }
  :min-lein-version "2.3.4"
  :global-vars {*warn-on-reflection* true})
