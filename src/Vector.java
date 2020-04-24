public class Vector {
    private double x, y, z;
    private double limit = Double.MAX_VALUE;

    public Vector() {
        this(0, 0, 0);
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void set(double x, double y, double z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    public void add(Vector vector) {
        x += vector.x;
        y += vector.y;
        z += vector.z;
        limit();
    }

    public void subtract(Vector vector) {
        x -= vector.x;
        y -= vector.y;
        z -= vector.z;
        limit();
    }

    public void multiply(double value) {
        x *= value;
        y *= value;
        z *= value;
        limit();
    }

    public void divide(double value) {
        x /= value;
        y /= value;
        z /= value;
        limit();
    }

    public double mag() {
        return Math.sqrt(x * x + y * y);
    }

    public void limit() {
        double m = mag();
        if (m > limit) {
            double ratio = m / limit;
            x /= ratio;
            y /= ratio;
        }
    }

    public Vector clone() {
        return new Vector(x, y, z);
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}