\version "2.18.0"

%Encoded by David Meredith
%Started: 29 June 2016
%Completed: 30 June 2016
%Encoded from pages 25-29 of
%Barber, S. (1950). Sonata for Piano, Op.26. G. Schirmer, Inc., New York.
%Ed. 1971

\header {
	tagline = ""
}

barOne =   \relative c {%bar 1
	\stemUp
	<b g'>8[
	\change Staff="upper"
	\stemDown
	<ees' bes'>
	\change Staff="lower"
	\stemUp
	<fis, d'>
	<c gis'>
	\change Staff="upper"
	\stemDown
	<e' a>
	\change Staff="lower"
	\stemUp
	<f, des'>]
}

barThreeLower = \relative c {
	\stemDown
	b8[ g' ees' bes' d, fis,]
}

barThreeUpper = \relative c {
	\stemDown
	r16 b''!8.~ b16[ c32 d ees16 d32 c] 
	b16[ ais~ ais32 ais ais cis]
}

barFourToSixUpper = \relative c'' {
	<<
		{ 
			\stemUp f!16[ e e8]~ e16[ g!32 e 
			\tuplet 3/2 {ees16 d c]}
			ces[ bes]~ bes r32 bes
			aes'16 g g8~ g16 c32 b! d cis e16 b ais cis8~ 
			%bar 6
			\stemDown 
			cis16[ c32 b \tuplet 3/2 {d16 des ees]} 
			a,16[ gis
			\acciaccatura gis des' c]
			\acciaccatura ees, a[ gis]
			\tuplet 6/4 {r32 fis[ cis f g,! c]}
		}
		\\
		{
			a8 gis r4 r8 r32 f32 f aes
			%\change Staff = "lower"
			d,!8[ des] r4 r4
		}
	>>
}

barFourToSixLower = \relative c {
	c8 gis' e' a des, f,!
	%bar 5
	\clef treble 
	e!8 des' bes' g' ees a,!
	%bar 6
	ges c, b d f gis
}

barSevenUpper = \relative c'' {
	b4~ b16 c32 d ees16 d32 c \stemUp b16 ais8 r16
}

barSevenLower = \relative c' {
	\clef bass
	<<
		{
			r8 g! ees' bes' \tuplet 3/2 {d,8[ ees,32 g]} fis8
		}
		\\
		{
			<ais, ais,>2
			<b! b,!>4
		}
	>>
}

barEightToTenUpper = \relative c' {
	%bar 8
	r8 <c g'> <g' ees'> <ees' bes'> <fis, d'> <c! aes'>
	%bar 9
	r8 \grace {f!16 bes} a!8~ a16 bes32 c 
	\tuplet 3/2 {des16 c bes}
	a16 gis8 r16
	%bar 10
	\time 2/4
	<<
		{a16 aes r8 aes!16 g r8}
		\\
		{c,?8 r c r}
	>>
}

barEightToTenLower = \relative c' {
	<<
		{b!4~ b16 c32 d32 ees16 d32 c b!16 ais~ ais32 ais ais cis 
			%bar 9
			a!8 f! des' gis \tuplet 3/2 {c,8 cis,32 f} e8~
			%bar 10
			\time 2/4
			\tuplet 3/2 {e16 r16 cis32 f} e16 r
			\tuplet 3/2 {r16 r cis32 f} e16 r
		}
		\\
		{
			<ais, ais,>2 <b! b,!>4
			<gis gis,>2 <a~ a,~>4
			<a, a'>8. r16 <ais' ais,>8. r16
		}
	>>
}

barElevenToThirteenUpper = \relative c' {
	\time 6/8
	\change Staff = "lower"
	g8[ 
	\change Staff = "upper"
	\stemDown
	<ees' bes'> 
	\change Staff = "lower"
	\stemUp
	<fis, d'> <c gis'>
	\change Staff = "upper"
	\stemDown
	<e' a!>
	\change Staff = "lower"
	\stemUp
	<f, des'>]
	%bar 12
	\change Staff = "upper"
	<<
		{b![ <ees' bes'> d c, <e' a> des]}
		\\
		{g,!4 fis8 gis4 f8}		
	>>
	%bar 13
	<<
		{
			<<
				{\grace {\omit TupletNumber \tuplet 3/2 {g,!16 b! g'!}}}
				\\
				{\stemUp \change Staff= "lower" \grace {b,,,!16 b'!}} 
				
			>>
			<g''! g'!>4 <fis~ fis'~>8 <fis fis'>8. <fis fis'>16 <f f'>8
		}
		\\
		{
			\change Staff="upper"
			b!8 <ees bes'> d <c, gis'> <e' a> des
		}
	>>
}

barElevenToThirteenLower = \relative c {
	\time 6/8
	<b b,>4. r
	%bar 12
	<b! g'!>8 <ees' bes'> <fis, d'> <c gis'> <e' a> <f, des'>
	%bar 13
	b!8 <ees bes'> <fis, d'> <c gis'> <e' a> <f, des'> 
}

barFourteenUpper = \relative c' {
	<e des'>16 <bes' g'> <a~ ees'~>8
	\tuplet 3/2 {<a ees'>16 <c, ges'> <b' f'>} <aes d>8
}

barFourteenLower = \relative c {
	\time 2/4
	\stemUp \grace {b,!16 b'!}
	g'8 ges aes f
}

barFifteenUpper = \relative c'' {
	\time 6/8
	\tuplet 6/4 {r16 r32 e[ f fis]}
	<gis ais>32[ fis f 
	\set stemRightBeamCount = #1
	e 
	\set stemLeftBeamCount = #1
	e! ees d des]
	\tuplet 6/4 {c!16.[ ees32 e f]}
	<g aes> f e 
	\set stemRightBeamCount = #1
	ees
	\set stemLeftBeamCount = #1
	ees d des c	
}

barFifteenLower = \relative c {
	<<
		aes'8
		\\
		{<c,, c'> <e'' b'!> <g, ees'> <cis, a'> <f' bes> <fis, d'!>}
	>>
}

barSixteenUpper = \relative c'' {
	\tuplet 6/4 {b!16[ g'32 a b c]}
	<c d> b bes 
	\set stemRightBeamCount = #1
	a
	\set stemLeftBeamCount = #1
	a aes g ges
	\tuplet 6/4 {f16[ g!32 a bes b]}
	<c des> b bes
	\set stemRightBeamCount = #1
	aes
	\set stemLeftBeamCount = #1
	aes g ges f
}

barSixteenLower = \relative c {
	\acciaccatura {<c, c'>} 
	aes''8 <e' b'!> <g, ees'> <cis, a'> <f' bes> <fis, d'!>
}

barSeventeenUpper = \relative c' {
	<des aes'>16[ <b'! e! b'!> <g~ ees'~>8]
	\tuplet 3/2 {<g ees'>16[ a <bes f' bes>} <fis d'!>8]
	r32 <f,! d'!> <b! aes'> <bes e!> r 
	\change Staff="lower"
	<des, g> 
	\change Staff="upper"
	<c' fis!> <a! ees'>
}

barSeventeenLower = \relative c {
	\acciaccatura {<c,! c'!>}
	aes''16[ <b! e!>
	<g~ ees'~>8] \tuplet 3/2 {<g ees'>16[ <cis, a'! cis> <bes' f'>}
	<fis d'>8]
	aes,16. g32 a16. fis32
}

barEighteenUpper = \relative c' {
	<<
		{<c! c'!>4 \tieNeutral <b~ b'~>8 <b b'>8. <b b'>16 <ais ais'>8}
		\\
		{e'!8 <gis dis'> g <f,! cis'> <a' d> fis}
	>>
}

barEighteenLower = \relative c, {
	<e! e'!>8 <gis' dis'> <b, g'> <f! cis'> <a' d> <ais, fis'>
}

barNineteenUpper = \relative c''' {
	\stemDown \tuplet 6/4 { r16 r32 gis[ a bes]}
	<c! d!> bes a 
	\set stemRightBeamCount = #1
	aes
	\set stemLeftBeamCount = #1
	aes g ges f
	\tuplet 6/4 {e16[ fis32 g gis a]}
	<b! c> a aes 
	\set stemRightBeamCount = #1
	g!
	\set stemLeftBeamCount = #1
	g ges f e
	
}

barNineteenLower = \relative c {
	<<
		c'!8
		\\
		{<e,, e'> \clef treble <gis'' dis'> <b,! g'> <f! cis'> <a' d!> <ais,! fis'!>}
	>>
}

barTwentyUpper = \relative c' {
	 \grace {c!16 e c'! c~} \stemUp <c e c'!>4
	 <b~ b'~>8 \tuplet 5/4 {<b b'>32 <b b'> <b b'> <b b'> 
	 	\set stemRightBeamCount = #1
	 	<b~ b'~>
	 }
	 \set stemLeftBeamCount = #1
	 <b b'> <b b'> <b b'> <d d'> <ais ais'>8
}

barTwentyLower = \relative c,, {
	\clef bass
	\grace {\omit TupletNumber \tuplet 3/4 {e16 e' e'~}} <e c'!>8
	\change Staff = "upper"
	<dis' gis b dis>
	<g,! b e g!>
	\change Staff = "lower"
	<cis, f! a cis>
	\change Staff = "upper"
	<d'! f a d!>
	\change Staff = "lower"
	<fis, ais cis fis>
}

barTwentyOneUpper = \relative c' {
	<<
		{
			<c! e g! c!>8 
			<dis' gis b dis>
			<g,! b e g>
			<cis, f! a cis>
			<d'! f a d!>
			<fis, a cis fis>
		}	
		\\
		{s2 <d! f a>8}
	>>
}

barTwentyOneLower = \relative c {
	<<
		{r16 <b b'>8 <c! c'!>32 
			\set stemRightBeamCount = #1
			<d! d'!>
			\set stemLeftBeamCount = #1
			<dis dis'>16 <d! d'!>32 <c c'>
		}
		\\
		{<e,, e'>4. 
			<b'' b'>16 
			\set stemRightBeamCount = #1
			\tieNeutral <bes~ bes'~> 
			\set stemLeftBeamCount = #1
			<bes bes'>32 <bes bes'> <bes bes'> <cis cis'>
			\tuplet 3/2 {<a cis fis a>8 <gis gis'>32 <fis fis'!>}
		}
	>>
}

barTwentyTwoUpper = \relative c {
	<<
		{
			r16 <f' f'!> 
			\set stemLeftBeamCount = #1
			\set stemRightBeamCount = #2
			<f' f'> <e~ e'~>8.
			\tuplet 5/4 {<e e'>32 <e e'> <e e'> <e e'> 
				\set stemRightBeamCount = #1
				<e~ e'~>
			}
			\set stemLeftBeamCount = #1
			<e e'> <e e'> <e e'> <g g'> dis'8
		}
		\\
		{<e,,,! f! a c! f!>8
			<dis'! gis b dis>
			<g,! b e g!>
			<f a cis>
			<f' a d!>
			dis'
		}
	>>
}

barTwentyTwoLower = \relative c {
	<<
		{s4. \tuplet 5/4 
			{r32 e'[ e e 
				\set stemRightBeamCount = #1
				e~
			} 
		\set stemLeftBeamCount = #1 
		e e e g
		\grace {\tuplet 3/2 {<fis,, ais>16 dis' <fis ais>}}
		dis'8]
		}
		\\
		{\stemUp <e,, a, e>4. \stemDown <cis f! a cis>8 <d'! f a d!> dis'}
	>>
}

barTwentyThreeUpper = \relative c {
	<<
		\relative c'' {
			r16 <fis fis'>8 <g! g'!>32 <a a'> <bes bes'>16 <a a'>32 
			<g g'>
			<ges ges'>16 <f!~ f'!~>
			\tuplet 5/4 {<f f'>32 <f f'> <f f'> <f f'> <gis gis'>}
			<e e'>8
		}
		\\
		\relative c'{<e g c!>8 <dis, gis b> d'''! cis <f,,! a d!> ais'}
	>>
}



barTwentyThreeLower = \relative c' {
	<<
		\clef treble
		\relative c' {
			\stemUp r16 fis8 g!32 a bes16 a32 g
			\stemUp ges16 f~ \tuplet 5/4 {f32 f f f gis} <e, fis ais e'>8
		}
		\\
		\new Staff {
			\once \omit Staff.TimeSignature
			\clef bass
			<<
				\relative c {
					s2 s8
					r16 r64 f! des \acciaccatura ees8 c!64
				}
				\\
				\relative c {			
					\stemDown
				<e g c!>8[ <dis, gis b> 
				<g' bes d!>
				<cis, f! a>
				<d! f a d>
				\acciaccatura {<fis,, fis'~>8}
				<fis~ fis'~>16
				<fis fis'>64
				<f f'>
				<des des'>
				\acciaccatura {<ees ees'>8} <c! c'!>64]
				}
			>>
		}
	>>
}

barTwentyFourUpper = \relative c'''' {
	r16
	\ottava #1
	\stemDown
	<ees bes'> <fis,~ d'~>8 <fis d'>32
	\ottava #0
	<c gis'> <e,! a> <f,! des'>
	<b,! g'!>16 <ees'' bes'> <ees bes'>
	<fis,~ d'!~> <fis d'>32 
	\set stemRightBeamCount = #2
	<c gis'>
	\set stemLeftBeamCount = #2
	\tuplet 3/2 {<e,! a> <c f!> <f,! des'>}
	
}

barTwentyFourLower = \relative c {
	<<
		{
				\relative c'' {
					\clef bass
					r16 
					\ottava #1
					<ees bes'> \tieNeutral <fis,~ d'~>8 <fis d'>32
					\ottava #0
					<c gis'> 
					<e,! a> <f,! des'>
					<b,! g'!>16 <ees'' bes'> <ees bes'>
					<fis,~ d'!~> <fis d'>32 
					\set stemRightBeamCount = #2
					<c gis'>
					\set stemLeftBeamCount = #2
					\tuplet 3/2 {<e! a> <c f!> <f,! des'>}
				}
		}
		\\
			\stemUp b2.
			\\
			\stemDown <b,, b'>2.
	>>
}

barTwentyFiveUpper = \relative c {
	\time 3/8
	\clef bass
	<b! g'!>32[ <ees' bes'>
	<fis, d'> <c gis'>]
	<e! a>[
	<f,! des'> <b,! g'!>
	<ees' bes'>]
	<fis,! d'!>[ <c gis'>
	<e! a>
	<f,! des'>]	
}

barTwentyFiveLower = \relative c {
	\stemNeutral <b,! g'!>16 r16 r8 r8
}

barTwentySixUpper = \relative c' {
	\time 4/4
	\clef treble
	<<
		{r4 r32 e'32 f fis <aes bes> g ges f!
		r4 r32 e32 f fis <aes bes> g ges f!}
		\\
		{\stemUp r8 <a,! a,>16 <aes~ aes,~> <aes aes,>4 r8 <a a,>32 <a a,> <a a,> <aes~ aes,~> <aes aes,>4}
		\\
		{\stemDown r8 <c,! e c'!>4. r8 <c! e c'!>4.}
	>>
}

barTwentySixLower = \relative c {
	<<
		\new Staff {
			\clef bass
			\stemDown
			\ottava #-1
			<ais,, ais'>2 
			<ais! ais'!>2
		}
		\\
		{
			r8 <ais'' e'>4.
			r8 <ais! e'>4.
		}
		\\
		{
			r4 \tuplet 3/2 {r16 r16 <cis cis'>32 <f! f'!>}  <e e'>8 
			r4 \tuplet 3/2 {r16 r16 <cis cis'>32 <f! f'!>}  <e e'>8 
		}
	>>
}

barTwentySevenUpper = \relative c' {
	\time 4/4
	\clef treble
	<<
		{
			r4 r32 b'32 c cis <ees fes> d des c!
			r4 r32 b,32 c cis ees d des 
			\slashedGrace {ees8} 
			c!32
		}
		\\
		{\stemDown r8 <aes'! aes,>16 <g~ g,~> <g g,>4 r8 
			\tuplet 3/2 {aes,!16 aes g~} g4
		}
		\\
		{\stemUp r8 <c! e c'!>4.}
	>>
}

barTwentySevenLower = \relative c {
	<<
		\new Staff {
			\clef bass
			\stemDown
			\ottava #-1
			<ais,, ais'>2 
			\ottava #0
			ais'!2
		}
		\\
		{
			r8 ais'4.
			r8 <ais! e'>4.
		}
		\\
		{
			r4 \tuplet 3/2 {r16 r16 cis 32 f}  e!8 
			r4 \tuplet 3/2 {r16 r16 cis!32 f}  e!8 
		}
	>>
}

barTwentyEightToThirtyOneUpper = \relative c'' {
	\time 3/4
	<<
		\relative c'' {
			r16 \tieNeutral b!8.~ b16 c32 d ees16 d32 c b16 ais8 r16
			%bar 29
			f'!16 e \tieUp e8~ e16 g!32 e \tuplet 3/2 {ees16 d c} ces bes r16. bes32
			%bar 30
			aes'16 g! g8~ g16 c32 
			\set stemRightBeamCount = #2
			b! 
			\set stemLeftBeamCount = #2
			d cis e16 b ais cis8~
			%bar 31
			cis16 c32 b \tuplet 3/2 {d16 des ees} a, gis
			\slashedGrace gis!8 des'!16 c a gis \tuplet 6/4 {r32 fis! cis f g,! c}
		}
		\\
		\relative c' {
			s2 r16 fis8 g32 gis
			%bar 29
			a16 gis32 fis f16 e r4 r16 e!32 f \tuplet 3/2 {ges16 f aes}
			%bar 30
			d,!8 des?16. g!32 f'16 e! e8 r16 fis32 eis gis g a16
			%bar 31
			e!16 ees8.~ ees16 ees32 d! \tuplet 3/2 {fis16 eis gis}
			e16. ees32 d!16 r16
		}
	>>
}

barTwentyEightToThirtyOneLower = \relative c, {
	<b! b'!>8 g'' ees' bes' d, fis,
	%bar29
	c gis' e' a des, f,!
	%bar 30
	\clef treble
	e des' bes' g' ees a,!
	%bar 31
	ges c, b d! f! gis 
}

barThirtyTwoUpper = \relative c'' {
	b4~ b16 c32 d ees16 d32 c
	<<
		\tuplet 6/4 {r16 <dis' ais'> <fis, d'> <c gis'> <e! a!> <f,! des'>}
		\\
		{
			<d b'!>16
			ais'8.
		}
	>>
}

barThirtyTwoLower = \relative c {
	\clef bass
	<<
		{
			r8 g'[ ees' bes']
		}
		\\
		{
			<ais,,, ais'>2 <b! b'!>4
		}
	>>
}

barThirtyThreeUpper = \relative c' {
	<<
		\relative c''{
			\stemUp
			%bar 33
			s2 
			%bar 34
			r16 cis32 d <ees bes'>8 <fis, d'> <c! gis'> <e'! a> <f, des'>
			%bar 35
			<b,! g'!> <ees' bes'> <fis, d'> <c gis'> <e'! a> <f,! des'>
			%bar 36
			\time 7/8
			<b,! g'!>[ <ees' bes'> <fis, d'> <e'! a> <f,! des'> <b,! g'> 
			\tieNeutral <ees~ ais~>]
			%bar 37
			<ees ais> <fis, d'!> <ees'~ ais~> <ees ais> <fis, d'> <ees'~ ais~>
			%bar 38
			<ees ais> <fis, d'> <ees'~ ais~> <ees ais> <fis, d'> <ees'~ ais~>
			%bar 39
			<ees ais>4. <fis, d'> ^\fermata
			
		}
		\\
		{
	\time 2/4
	\stemNeutral 
	\slurNeutral
	\tieNeutral
	<b~ g'!>8 b16 c32 d ees16 d32 
	\set stemRightBeamCount = #1
	ees~ 
	\set stemLeftBeamCount = #1
	\tupletUp \tuplet 3/2 {ees16 d c}
	%bar 34
	\time 6/8
	\showStaffSwitch
	b!8 
	\change Staff = "lower"
	bes4~ bes8~ bes32 bes bes des a8~
	%bar 35
	a8 fis4~ fis16 fis32 a f4~ 
	%bar 36
	\time 7/8
	\stemUp f8 \stemNeutral cis4~ cis16 c8 cis32 dis \tuplet 3/2 {e16 dis cis} c8
	%bar 37
	\time 6/8
	<b, b'>4. <gis gis'>4.
	<b b'>4. <g! g'!>4.
	%bar 39
	<b, b'>2. ^\fermata \bar "|."			
		}
	>>
	
}

barThirtyThreeLower = \relative c {
	\time 2/4
	<<
		{
			r16 c32 g' ees' \clef treble ees bes' ees bes'4
			\clef bass
		}
		\\
		\relative c, {
			<ais ais'>2
			<b,! b'!>2.
			<b! b'!>2.
			<b! b'!>2. r8
		}
	>>
}


upper = \relative c' {
  \clef treble
  \key c \major
  \time 6/8
  s2.
  s2.
  \barThreeUpper
  \barFourToSixUpper
  \barSevenUpper
  \barEightToTenUpper
  \barElevenToThirteenUpper
  \barFourteenUpper
  \barFifteenUpper
  \barSixteenUpper
  \barSeventeenUpper
  \barEighteenUpper
  \barNineteenUpper
  \barTwentyUpper
  \barTwentyOneUpper
  \barTwentyTwoUpper
  \barTwentyThreeUpper
  \barTwentyFourUpper
  \barTwentyFiveUpper
  \barTwentySixUpper
  \barTwentySevenUpper
  \barTwentyEightToThirtyOneUpper
  \barThirtyTwoUpper
  \barThirtyThreeUpper
}

lower = \relative c {
  \clef bass
  \key c \major
  \time 6/8
  \barOne
  \barOne
  \time 3/4
  \barThreeLower
  \barFourToSixLower
  \barSevenLower
  \barEightToTenLower
  \barElevenToThirteenLower
  \barFourteenLower
  \barFifteenLower
  \barSixteenLower
  \barSeventeenLower
  \barEighteenLower
  \barNineteenLower
  \barTwentyLower
  \barTwentyOneLower
  \barTwentyTwoLower
  \barTwentyThreeLower
  \barTwentyFourLower
  \barTwentyFiveLower
  \barTwentySixLower
  \barTwentySevenLower
  \barTwentyEightToThirtyOneLower
  \barThirtyTwoLower
  \barThirtyThreeLower
}

\score {
  \new PianoStaff <<
    \set PianoStaff.instrumentName = #"Piano  "
    \new Staff = "upper" \upper
    \new Staff = "lower" \lower
  >>
  \layout { }
  \midi { }
}