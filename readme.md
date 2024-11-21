# Testing inline hydration using RUM

This demo shows a very basic way of rendering static HTML with some
components being re-hydrated on the frontend.

## watch the browser js, to start js dev

static built files include this js

```
npm i
npx shadow-cljs watch browser
```

## start a clojure repl

Currently you can build the pages only from the repl. no commands made yet

```
clj -M:repl
```

After connecting the editor, in `build.clj`, excecute

```clojure
(spit "./public/index.html" (rum/render-html (app/app "helloooo")))
```
