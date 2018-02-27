package drive;

/**
 * Can be implemented for drive systems that support switching the direction of driving
 * @author Daniel
 *
 */
public interface Reversible {
	/**
	 * Reverses the direction that the drive system drives
	 * @param reverse if the drive should be reversed
	 */
	public void reverseDrive(boolean reverse);
}
