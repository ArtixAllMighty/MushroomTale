package Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import GameState.WorldState;
import Main.GamePanel;
import TileMap.Tile;
import TileMap.TileMap;

public abstract class MapObject {

	// tile stuff
	protected TileMap tileMap;
	protected int tileSize;
	protected double xmap;
	protected double ymap;

	// position and vector
	protected double x;
	protected double y;
	protected double dx;
	protected double dy;

	// dimensions
	protected int width;
	protected int height;

	// collision box
	protected int cwidth;
	protected int cheight;

	// collision
	protected int currRow;
	protected int currCol;
	protected double xdest;
	protected double ydest;
	protected double xtemp;
	protected double ytemp;
	protected boolean topLeft;
	protected boolean topRight;
	protected boolean bottomLeft;
	protected boolean bottomRight;

	protected boolean tileHurtsPlayer = false;

	// animation
	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	/**
	 * All enemies are drawn facing Left, so by default, facingRight has to be
	 * false to make them look Right.
	 */
	protected boolean facingRight;

	// movement
	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;
	protected boolean jumping;
	protected boolean falling;

	// movement attributes
	protected double moveSpeed;
	protected double maxSpeed;
	protected double stopSpeed;
	protected double fallSpeed;
	protected double maxFallSpeed;
	protected double jumpStart;
	protected double stopJumpSpeed;

	protected boolean isFlightEnabled = false;

	private WorldState world;

	public static boolean showBox = false;

	// constructor
	public MapObject(TileMap tm) {
		tileMap = tm;
		tileSize = tm.getTileSize();
	}

	public void calculateCorners(double x, double y) {

		final int leftTile = (int) (x - (cwidth / 2)) / tileSize;
		final int rightTile = (int) ((x + (cwidth / 2)) - 1) / tileSize;
		final int topTile = (int) (y - (cheight / 2)) / tileSize;
		final int bottomTile = (int) ((y + (cheight / 2)) - 1) / tileSize;

		final int tl = tileMap.getType(topTile, leftTile);
		final int tr = tileMap.getType(topTile, rightTile);
		final int bl = tileMap.getType(bottomTile, leftTile);
		final int br = tileMap.getType(bottomTile, rightTile);

		topLeft = tl == Tile.BLOCKED;
		topRight = tr == Tile.BLOCKED;
		bottomLeft = bl == Tile.BLOCKED;
		bottomRight = br == Tile.BLOCKED;
	}

	public void checkTileMapCollision() {

		currCol = (int) x / tileSize;
		currRow = (int) y / tileSize;

		xdest = x + dx;
		ydest = y + dy;

		xtemp = x;
		ytemp = y;

		calculateCorners(x, ydest);
		if (dy < 0)
			if (topLeft || topRight) {
				dy = 0;
				ytemp = (currRow * tileSize) + (cheight / 2);

			} else
				ytemp += dy;
		if (dy > 0)
			if (bottomLeft || bottomRight) {
				dy = 0;
				falling = false;
				ytemp = ((currRow + 1) * tileSize) - (cheight / 2);
			} else
				ytemp += dy;

		calculateCorners(xdest, y);
		if (dx < 0)
			if (topLeft || bottomLeft) {
				dx = 0;
				xtemp = (currCol * tileSize) + (cwidth / 2);
			} else
				xtemp += dx;
		if (dx > 0)
			if (topRight || bottomRight) {
				dx = 0;
				xtemp = ((currCol + 1) * tileSize) - (cwidth / 2);
			} else
				xtemp += dx;

		if (!falling) {
			calculateCorners(x, ydest + 1);
			if (!bottomLeft && !bottomRight)
				falling = true;
		}
	}

	public void draw(Graphics2D g) {
		drawSprite(g, animation);
	}

	private void drawSprite(Graphics2D g, Animation am) {

		if (showBox) {
			// Rectangle r = getRectangle();
			// g.setColor(Color.WHITE);
			// g.draw(r);
		}

		setMapPosition();

		// draw

		if (facingRight)
			g.drawImage(animation.getImage(), (int) ((x + xmap) - (width / 2)),
					(int) ((y + ymap) - (height / 2)), null);
		else
			g.drawImage(animation.getImage(),
					(int) (((x + xmap) - (width / 2)) + width),
					(int) ((y + ymap) - (height / 2)), -width, height, null);
	}

	public int getCHeight() {
		return cheight;
	}

	public int getCWidth() {
		return cwidth;
	}

	public int getHeight() {
		return height;
	}

	public void getNextPosition() {

		if (left) {
			dx -= moveSpeed;
			if (dx < -maxSpeed)
				dx = -maxSpeed;
		} else if (right) {
			dx += moveSpeed;
			if (dx > maxSpeed)
				dx = maxSpeed;
		}

		if (falling && !isFlightEnabled)
			dy += fallSpeed;
	}

	public Rectangle getRectangle() {
		return new Rectangle((int) x - cwidth, (int) y - cheight, cwidth,
				cheight);
	}

	public int getWidth() {
		return width;
	}

	/** can be null */
	public WorldState getWorld() {
		return world;
	}

	public int getx() {
		return (int) x;
	}

	public int gety() {
		return (int) y;
	}

	public boolean intersects(MapObject o) {
		final Rectangle r1 = getRectangle();
		final Rectangle r2 = o.getRectangle();
		return r1.intersects(r2);
	}

	public boolean notOnScreen() {
		return ((x + xmap + width) < 0)
				|| (((x + xmap) - width) > GamePanel.WIDTH)
				|| ((y + ymap + height) < 0)
				|| (((y + ymap) - height) > GamePanel.HEIGHT);
	}

	public void setDown(boolean b) {
		down = b;
	}

	public void setJumping(boolean b) {
		jumping = b;
	}

	public void setLeft(boolean b) {
		left = b;
	}

	public void setMapPosition() {
		xmap = tileMap.getx();
		ymap = tileMap.gety();
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setRight(boolean b) {
		right = b;
	}

	public void setUp(boolean b) {
		up = b;
	}

	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public void setWorld(WorldState w) {
		world = w;
	}

}
