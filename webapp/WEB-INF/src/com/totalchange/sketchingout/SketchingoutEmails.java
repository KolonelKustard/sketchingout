/*
 * Created on 08-Dec-2004
 */
package com.totalchange.sketchingout;

/**
 * @author RalphJones
 *
 * <p>Contains an array of emails</p>
 */
public class SketchingoutEmails {
	public static final String SUBST_FROM_NAME = ":from_name:";
	public static final String SUBST_FROM_EMAIL = ":from_email:";
	public static final String SUBST_TO_NAME = ":to_name:";
	public static final String SUBST_TO_EMAIL = ":to_email:";
	public static final String SUBST_BODY_PART = ":body_part:";
	public static final String SUBST_A_OR_SOME = ":a_or_some:";
	public static final String SUBST_THOSE_ARE_OR_THAT_IS = ":those_are_or_that_is:";
	public static final String SUBST_URL = ":url:";

	public static final String CONST_SUBJECT = "Here's your drawing from SketchingOut.co.uk";
	
	public static final int EMAILS_FROM_NAME = SketchingoutEmail.EMAILS_ARRAY_FROM_NAME;
	public static final int EMAILS_FROM_EMAIL = SketchingoutEmail.EMAILS_ARRAY_FROM_EMAIL;
	public static final int EMAILS_SUBJECT = SketchingoutEmail.EMAILS_ARRAY_SUBJECT;
	public static final int EMAILS_BODY = SketchingoutEmail.EMAILS_ARRAY_BODY;
	public static final String[][] EMAILS = {
		{
			"Bobby McFerrin",
			"bobbymcferrin@sketchingout.co.uk",
			CONST_SUBJECT,
			"Hi " + SUBST_TO_NAME + ",\n\n" +
			"The drawing you did on SketchingOut is completed! We think your " + SUBST_BODY_PART + " makes the drawing...!\n\n" +
			"Kind regards,\n\n" +
			"Bob"
		}, 
		{
			"Jimmy Jim",
			"jimmyjim@sketchingout.co.uk",
			CONST_SUBJECT,
			"What on earth is this? its your completed drawing from SketchingOut. Thats what.\n\n" +
			"Best washes,\n\n" +
			"jim"
		},
		{
			"Wailing Tuff",
			"wailingtuff@sketchingout.co.uk",
			CONST_SUBJECT,
			"Here's your drawing. Remember? SketchingOut? oh well, just have a look anyway.\n\n" +
			"Love,\n\n" +
			"Wailing T"
		},
		{
			"Devious Honey R. Smooth",
			"devious@sketchingout.co.uk",
			CONST_SUBJECT,
			"You have Won...! ...A drawing. Enjoy your prize.\n\n" +
			"All the best,\n\n" +
			"Mr. Smooth"
		},
		{
			"Stealth Maestro leech Loco",
			"stealth@sketchingout.co.uk",
			CONST_SUBJECT,
			"Crikey, look at the state of this! We think its ace. well done.\n\n" +
			"Peace,\n\n" +
			"S. Loco"
		},
		{
			"D. Magical Robinson Clinton",
			"d_mag@sketchingout.co.uk",
			CONST_SUBJECT,
			"Have you seen this? well have a look, its your finished drawing!\n\n" +
			"Massive respect,\n\n" +
			"Mr. Magic"
		},
		{
			"Master Fly Beazer Slick",
			"fly@sketchingout.co.uk",
			CONST_SUBJECT,
			"Does this look normal to you? " +SUBST_THOSE_ARE_OR_THAT_IS+ " the strangest " + SUBST_BODY_PART + " We've ever seen!\n\n" +
			"Shhhh,\n\n" +
			"Fly"
		},
		{
			"Mack P. Bobby Kicks",
			"mack@sketchingout.co.uk",
			CONST_SUBJECT,
			"Hey, your drawing is done! lets take a look, arrgghhh! it's hideous!\n\n" +
			"Check yourself,\n\n" +
			"Mack"
		},
		{
			"Adam Ant",
			"adamant@sketchingout.co.uk",
			CONST_SUBJECT,
			"Is this not the best picture you've ever seen?!\n\n" +
			"Kind regards,\n\n" +
			"The Dandy Highwayman"
		},
		{
			"Shakin' Stevens",
			"shakey@sketchingout.co.uk",
			CONST_SUBJECT,
			"I lied about the old house, its rubbish, unlike this picture!\n\n" +
			"Keep on Rockin',\n\n" +
			"Shakey"
		},
		{
			"Your Mum",
			"mumsie@sketchingout.co.uk",
			CONST_SUBJECT,
			"Hey that looks like my mother! how dare you!\n\n" +
			"Tsk,\n\n" +
			"Mum"
		},
		{
			"A Wellwisher",
			"wellwisher@sketchingout.co.uk",
			CONST_SUBJECT,
			"This picture would have been fantastic. If you hadn't drawn " + SUBST_A_OR_SOME + " stupid " + SUBST_BODY_PART + "!\n\n" +
			"Wishing you well,\n\n" +
			"From nobody in particular"
		},
		{
			"E.T.",
			"et@sketchingout.co.uk",
			CONST_SUBJECT,
			"Your picture is ready! I bet the other drawers are glad you were involved...\n\n" +
			"Stay tuned,\n\n" +
			"E.T"
		},
		{
			"Boney M",
			"boneym@sketchingout.co.uk",
			CONST_SUBJECT,
			"Wow, what an ace piece of drawing teamwork!\n\n" +
			"You've made my day,\n\n" +
			"Boney"
		},
		{
			"David Bellamy",
			"daveb@sketchingout.co.uk",
			CONST_SUBJECT,
			"Here's your finished drawing. Make of it what you will.\n\n" +
			"Lovely,\n\n" +
			"Mr. B"
		},
		{
			"Rolf Harris",
			"daveb@sketchingout.co.uk",
			CONST_SUBJECT,
			"Can you guess what this is yet? No, me neither...!\n\n" +
			"G'Day,\n\n" +
			"Rolf"
		},
		{
			"Bobby Bones",
			"bb@sketchingout.co.uk",
			CONST_SUBJECT,
			"Here's your drawing. We hope you like it. If you don't, we really are very sorry.\n\n" +
			"Hope you get better soon,\n\n" +
			"Bob"
		},
		{
			"Denzil",
			"denzil@sketchingout.co.uk",
			CONST_SUBJECT,
			"Here's your drawing. Wasn't it worth the wait?!\n\n" +
			"I think so,\n\n" +
			"Denzil"
		},
		{
			"Frankie",
			"frankie@sketchingout.co.uk",
			CONST_SUBJECT,
			"Here's your completed drawing. My 2 year old sister couldn't have done any better...\n\n" +
			"Relax, don't do it,\n\n" +
			"Frank"
		}
	};
	
	public static final String[][] EMAILS_TO_FRIENDS = {
		{
			"SketchingOut",
			"whoareyou@sketchingout.co.uk",
			"A SketchingOut.co.uk drawing request",
			"Hi " + SUBST_TO_NAME + ",\n\n" +
			"Your Pal, " + SUBST_FROM_NAME + " " + SUBST_FROM_EMAIL + "has invited you to draw a picture!" + "\n" +
			"Kind regards,\n\n" +
			"www.SketchingOut.co.uk"
		}
	};
}
