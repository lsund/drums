# Drums

A drum machine is used compose drum patterns. You can use these patterns
example when producing a new piece of electronic music, or as a complement if
your band lacks a drummer.

The basic layout of a drum machine is a sequence of buttons (mostly 16) laid
out in a row. Each button represents a *step* (or a 16th note), 4 steps makes
up one  *beat* and four beats make up one *bar*. When you press *play* on the
drum machine, playback starts. This is visualized by each step-buttons lighting
up shortly at the corresponding 16th note in the playback loop.

You won't hear anything yet though, a drum machine only plays the corresponding
note if you press one of the step-buttons to *activate* it.  A typical drum
machine gives you access to multiple tracks. Each track can have a different
instrument associated to it, so that you can program Kick, snare and high-hat
at the same time, for example.

## What this is and why it was done

A drum machine simulation. Tested on Chrome v. 87 and Firefox 83 (Linux). It
uses HTMLAudioElement to play sound. It was made as an example usage of
clojurescript, core.async and react.js. The goal is to showcase an approach to
UI's where state management is completely decoupled from the rendering logic.
Notice that the components in `src/cljs/drums/components` only depends on
`event-channel`, they communicate with the outside only through immutable data.
This is a straightforward strategy for writing reusable components.

The server part (in `src/clj`) is just for deployment purposes.

## Local

```
lein figwheel # localhost:3449
# OR
lein run # localhost:1338
```

## Heroku

https://drums-lsund.herokuapp.com/

For free apps, heroku puts a 30s sleep timer before the app responds.

