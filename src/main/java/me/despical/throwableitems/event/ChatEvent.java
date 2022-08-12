package me.despical.throwableitems.event;

import me.despical.throwableitems.Main;
import me.despical.throwableitems.util.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @author Despical
 * <p>
 * Created at 4.08.2022
 */
public class ChatEvent extends ListenerAdapter {

	public ChatEvent(Main plugin) {
		super(plugin);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String text = event.getMessage();

		test(text, player.getLocation());
	}


	public static BufferedImage stringToBufferedImage(Font font, String s) {
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = img.getGraphics();
		g.setFont(font);

		FontRenderContext frc = g.getFontMetrics().getFontRenderContext();
		Rectangle2D rect = font.getStringBounds(s, frc);
		g.dispose();

		img = new BufferedImage((int) Math.ceil(rect.getWidth()), (int) Math.ceil(rect.getHeight()), BufferedImage.TYPE_4BYTE_ABGR);
		g = img.getGraphics();
		g.setColor(Color.black);
		g.setFont(font);

		FontMetrics fm = g.getFontMetrics();
		int x = 0;
		int y = fm.getAscent();


		g.drawString(s, x, y);
		g.dispose();

		return img;
	}

	static public final float PI = 3.1415927f;
	static public final float degreesToRadians = PI / 180;
	static public Font font = new Font("Tahoma", Font.PLAIN, 16);


	public void test(String text, Location location) {
		Particle particle = Particle.FLAME;

		boolean invert = false;

		int stepX = 1;
		int stepY = 1;
		float size = (float) 1 / 5;

		boolean realtime = false;


		final BufferedImage[] image = {null};

		final String[] lastParsedText = {null};

		final Font[] lastParsedFont = {null};

		new BukkitRunnable() {
			@Override
			public void run() {
				int clr = 0;
				try {
					if (image[0] == null || shouldRecalculateImage(text)) {
						lastParsedText[0] = text;
						lastParsedFont[0] = font;
						image[0] = stringToBufferedImage(lastParsedFont[0], lastParsedText[0]);
					}
					for (int y = 0; y < image[0].getHeight(); y += stepY) {
						for (int x = 0; x < image[0].getWidth(); x += stepX) {
							clr = image[0].getRGB(x, y);
							if (!invert && Color.black.getRGB() != clr) {
								continue;
							} else if (invert && Color.black.getRGB() == clr) {
								continue;
							}

							Vector v = new Vector((float) image[0].getWidth() / 2 - x, (float) image[0].getHeight() / 2 - y, 0).multiply(size);
							Utils.rotateAroundAxisY(v, -location.getYaw() * degreesToRadians);
							location.getWorld().spawnParticle(particle, location.add(v), 0);
							location.subtract(v);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.runTaskTimer(plugin, 20, 1);

	}

	public static boolean objectsEquals(Object a, Object b) {
		return Objects.equals(a, b);
	}

	private boolean shouldRecalculateImage(String text) {
		return false;
	}
}