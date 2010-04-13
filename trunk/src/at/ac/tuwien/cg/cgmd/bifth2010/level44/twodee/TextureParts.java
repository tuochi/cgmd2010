package at.ac.tuwien.cg.cgmd.bifth2010.level44.twodee;


public class TextureParts {
	
	/* Parts from the first 256x256 texture */
	
	public static TexturePart makeRabbitHead(Texture texture) {
		return new TexturePart(texture, 0, 128, 191, 255);
	}
	
	public static TexturePart makeWing(Texture texture) {
		return new TexturePart(texture, 0, 0, 127, 127);
	}
	
	public static TexturePart getBucket(Texture texture) {
		return new TexturePart(texture, 128, 0, 255, 127);
	}
	
	public static TexturePart getCoin(Texture texture) {
		return new TexturePart(texture, 224, 224, 255, 255);
	}
	
	/* Parts from the landscape texture */
	
	public static TexturePart makeSky(Texture texture) {
		return new TexturePart(texture, 0, 0, 0+10, 0+320);
	}

	public static TexturePart makeCloud(Texture texture, int id) {
		int pos = id%3;
		
		switch (pos) {
			case 1:
				return new TexturePart(texture, 280, 0, 280+133, 0+188);
			case 2:
				return new TexturePart(texture, 20, 218, 20+200, 218+144);
			default:
				return new TexturePart(texture, 20, 0, 20+250, 0+195);
		}
	}

	public static TexturePart makeMeadow(Texture texture) {
		return new TexturePart(texture, 0, 382, 0+930, 382+130);
	}

	public static TexturePart makeHills(Texture texture) {
		return new TexturePart(texture, 234, 242, 234+790, 242+120);
	}

	public static TexturePart makeMountains(Texture texture) {
		return new TexturePart(texture, 420, 0, 420+600, 0+210);
	}

	public static TexturePart makeCrosshairsRed(Texture texture) {
		return new TexturePart(texture,48,0,96,48);
	}
	
	public static TexturePart makeCrosshairsGreen(Texture texture) {
		return new TexturePart(texture,0,0,48,48);
	}
}
