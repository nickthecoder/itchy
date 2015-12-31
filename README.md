If you want to write games, then Itchy takes care of the hard stuff,
while you take care of the fun, creative stuff!

Itchy is an easy to use 2D Java game engine.
Its aim is to make writing games, fun, simple and hassle free, without imposing limits.
You can write professional quality 2D games using Itchy.

It is designed to be fast, using SDL as its low level graphics API, via a java wrapper called "Jame".

Features
========

High performace - easily redraws the whole screen 60 frames per second.

Highly optimised pixel perfect collision detection. (No need to manually draw collision boundaries).

A choice of scripting languages (Python, Groovy, and Javascript).

An in-built scene editor, to design your game's levels.

Resources editor - add sound and animation to your game without writing any code.

On-the-fly sprite manipulations: rotation, scale, fade, colorize...

A complete GUI toolkit, which can be skinned to suit your game (or use the default skin).

Define custom properties, for each of your game objects which can then be edited in the scene designer.


Build from Source
=================

You first need to get hold of Jame (https://github.com/nickthecoder/jame), which handles the low level
graphics, sound and user input.

Itchy uses the gradle build tool. Here's some useful targets :


    gradle compile
    gradle extractNatives 
    gradle installApp
    
The "extractNatives" target extracts the dll/so files from jame's jar files.

Play
====

Linux :

./launch

Windows :

Run the "launch.vbs" file

