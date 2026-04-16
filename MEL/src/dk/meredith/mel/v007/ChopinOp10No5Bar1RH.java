package dk.meredith.mel.v007;

public class ChopinOp10No5Bar1RH extends MEL {

	public ChopinOp10No5Bar1RH () {
		MaskStructure majorScale = maskStructure(2,2,1,2,2,2,1);
		MaskStructure triad = maskStructure(2,2,3);
		MaskStack     quaverLevel = maskStack(mask(0,2));
		MaskStack     semiquaverLevel = maskStack(mask(0,1));
		MaskStack     gFlatMajorTriad = maskStack(mask(6,majorScale),
					                          mask(0,triad));
		Space         s1 = space(quaverLevel,gFlatMajorTriad);
		System.out.println("s1 = "+s1);
		Vector        v1 = vector(1,-1);
		System.out.println("v1 = "+v1);
		VectorSet     vs1 = vectorSet(s1,v1);
		System.out.println("vs1 = "+vs1);
		Space         s2 = space(semiquaverLevel,gFlatMajorTriad);
		Vector        v2 = vector(1,1);
		VectorSet     vs2 = vectorSet(s2,v2);
		System.out.println("vs2 = "+vs2);
		VectorSet     x = cross(vs1,vs2);
		System.out.println("x = "+x);
		
		add(union(pointSet(point(0,90),x)));
	}
	
	public static void main(String[] args) {
		System.out.println(new ChopinOp10No5Bar1RH());
	}

}
