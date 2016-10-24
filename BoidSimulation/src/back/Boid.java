package back;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class Boid extends Circle {

	float dx = -1.5f;
	float dy = -1.5f;
	public int mass;
	public Speed speed;
	public Boolean collided = false;
	private int delta = 1;
	public Point2D position;

	// constructor
	public Boid(float x, float y, int radius, float speedX, float speedY) {
		this.setCenterX(x);
		this.setCenterY(y);
		this.setRadius(radius);
		this.mass = radius;
		this.speed = new Speed(speedX, speedY);
		position = new Point2D (x, y);

	}

	// Getters and Setters.
	public float getDx() {
		return speed.x;
	}

	public void setDx(float dx) {
		this.speed.x = dx;
	}

	public float getDy() {
		return speed.y;
	}

	public void setDy(float dy) {
		this.speed.y = dy;
	}
	
	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}

	public boolean collide(Boid other) {

		// Determine it's size
		Boid otherBoid = other;
		Boid thisBoid = this;

		Point2D otherCenter = new Point2D(otherBoid.getCenterX() + otherBoid.getTranslateX() + otherBoid.getRadius(),
				otherBoid.getCenterY() + otherBoid.getTranslateY() + otherBoid.getRadius());
		Point2D thisCenter = new Point2D(thisBoid.getCenterX() + thisBoid.getTranslateX() + thisBoid.getRadius(),
				thisBoid.getCenterY() + thisBoid.getTranslateY() + thisBoid.getRadius());
		double xDistance = thisCenter.getX() - otherCenter.getX();
		double yDistance = thisCenter.getY() - otherCenter.getY();
		double distance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
		double minDistance = otherBoid.getRadius() + thisBoid.getRadius() + delta;


		return (distance < minDistance);
	}
	
	public boolean collide(Boid other, int d) {

		// Determine it's size
		Boid otherBoid = other;
		Boid thisBoid = this;

		Point2D otherCenter = new Point2D(otherBoid.getCenterX() + otherBoid.getTranslateX() + otherBoid.getRadius(),
				otherBoid.getCenterY() + otherBoid.getTranslateY() + otherBoid.getRadius());
		Point2D thisCenter = new Point2D(thisBoid.getCenterX() + thisBoid.getTranslateX() + thisBoid.getRadius(),
				thisBoid.getCenterY() + thisBoid.getTranslateY() + thisBoid.getRadius());
		double xDistance = thisCenter.getX() - otherCenter.getX();
		double yDistance = thisCenter.getY() - otherCenter.getY();
		double distance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
		double minDistance = otherBoid.getRadius() + thisBoid.getRadius() + d;

		return (distance < minDistance);
	}

}
