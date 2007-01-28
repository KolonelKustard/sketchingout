import com.totalchange.sketchingout.drawer.DrawerCanvasMovieClip;
import com.totalchange.sketchingout.drawer.JavaScriptClient;

import com.macromedia.javascript.JavaScriptProxy;

// Make sure not scaling stuff and everything is top left aligned
Stage.scaleMode = "noScale";
Stage.align = "TL";

// Create the main drawing canvas
var canvas: DrawerCanvasMovieClip = DrawerCanvasMovieClip(_root.attachMovie("Canvas", "canvas", _root.getNextHighestDepth()));
canvas._x = 20;
canvas._y = 20;
canvas._width = 500;
canvas._height = 300;

// Create and configure the JavaScript proxying thingy
var client: JavaScriptClient = new JavaScriptClient(canvas);
client.canvas = canvas;
var proxy: JavaScriptProxy = new JavaScriptProxy(_root.lcId, client);

btn.onPress = function() {
	canvas.undoStart();
}

btn.onRelease = function() {
	canvas.undoStop();
}

// STOP
stop();