\version "null"

\header {
	tagline = ""
}

upper = \relative c' {
	\clef treble
	\key e \major
	\time 2/4
	
	% anacrusis bar
	\partial 8
	b8
	
	% bar 1
	e8 dis16 e <<fis4 dis>>
}

lower = \relative c {
  	\clef bass
  	\key e \major
  	\time 2/4
  
  	% anacrusis bar
  	\partial 8
 	r8
 	
 	% bar 1
	<<
		\relative {\stemUp gis16 b gis b a b a b} \\
		\relative {\stemDown e,16 b'8 b16 b, b'8 b16} \\
		\relative {\stemUp e,4 b4} 
	>>
	
}

\score {
	
	\new PianoStaff <<
	 \new Staff = "upper" \upper
	 \new Staff = "lower" \lower		
	>>
	\layout {}
	\midi {}
}
