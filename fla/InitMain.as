_root.attachMovie(holderClip, "holderClip",1000);
holderClip.prevDrawing = lastDrawCanvas;
holderClip.nextDrawing = mainCanvas;
holderClip.userDetails.nameEdit = yourName_txt;
holderClip.userDetails.emailEdit = yourEmail_txt;
holderClip.userDetails.sigCanvas = sigCanvas;
holderClip.userDetails.clearSigButton = clearSig_btn;
holderClip.dragClip = dragClip_mc;
holderClip.submitButton = send_btn;
holderClip.friendsEmailEdit = friendsEmail_txt;
holderClip.clearDrawingButton = clearDrawing_btn;


