/*
 * Copyright (c) 2006-2008 Shane Mc Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package uk.org.dataforce.swing;

import javax.swing.JDesktopPane;
import javax.swing.JViewport;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.DefaultDesktopManager;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.Icon;
import javax.swing.Box;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;

import java.lang.reflect.Constructor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

import java.awt.Insets;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.beans.PropertyVetoException;

/**
 * Improved JDesktopPane.
 *
 * Originally based on code from:
 * http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi.html
 *
 * Other Changes:
 * - Frames can't end up with a negative x/y
 * - Respond to resize events of the JViewport parent
 * - Iconified icons move themselves to remain inside the desktop at all times.
 * - Handles maximised frames correctly (desktop doesn't scroll, no titlebar)
 */
public class DFDesktopPane extends JDesktopPane implements ComponentListener {
	/**
	 * A version number for this class.
	 * It should be changed whenever the class structure is changed (or anything
	 * else that would prevent serialized objects being unserialized with the new
	 * class).
	 */
	private static final long serialVersionUID = 200807301;
	
	/** Are we already resizing the desktop, or some other frames. */
	private boolean isResizing = false;
	
	/**
	 * Should we properly maximise frames?
	 * (This removes the titlebar and borders from frames when maximised to make
	 * them look less stupid - but this requires an alternative method to restore
	 * the window)
	 */
	private boolean properlyMaximise = true;
	
	/** What (if any) parent are we already listening to for resize events? */
	private Container listeningParent = null;
	
	/**
	 * Create a new DFDesktopPane that properly maximises frames.
	 */
	public DFDesktopPane() {
		this(true);
	}
	
	/**
	 * Create a new DFDesktopPane.
	 *
	 * @param properlyMaximise Properly maximise frames?
	 */
	public DFDesktopPane(final boolean properlyMaximise) {
		super();
		this.properlyMaximise = properlyMaximise;
		
		// Allow us to track child frames
		setDesktopManager(new DefaultDesktopManager() {
			/** A version number for this class. */
			private static final long serialVersionUID = 200807301;
			
			/** {@inheritDoc} */
			@Override
			public void endResizingFrame(final JComponent frame) {
				super.endResizingFrame(frame);
				resizeDesktop();
			}
			
			/** {@inheritDoc} */
			@Override
			public void endDraggingFrame(final JComponent frame) {
				super.endDraggingFrame(frame);
				resizeDesktop();
			}
			
			/** {@inheritDoc} */
			@Override
			public void maximizeFrame(final JInternalFrame frame) {
				super.maximizeFrame(frame);
				handleMaximizeFrame(frame);
			}
			
			/** {@inheritDoc} */
			@Override
			public void minimizeFrame(final JInternalFrame frame) {
				super.minimizeFrame(frame);
				handleMinimizeFrame(frame);
				resizeDesktop();
			}
			
			/** {@inheritDoc} */
			@Override
			public void activateFrame(final JInternalFrame frame) {
				super.activateFrame(frame);
				
				// If another frame is maximised, make sure we are also
				synchronized (this) {
					if (isResizing) { return; }
					else { isResizing = true; }
				}
				if (!frame.isMaximum()) {
					for (JInternalFrame internalFrame : getAllFrames()) {
						if (internalFrame != frame && internalFrame.isMaximum()) {
							try {
								frame.setMaximum(true);
							} catch (PropertyVetoException pve) { }
							break;
						}
					}
				}
				synchronized (this) { isResizing = false; }
			}
		});
		
		// Try to track our scroll pane when we get one
		addComponentListener(this);
	}
	
	/** {@inheritDoc} */
	@Override
	public void componentResized(final ComponentEvent event) {
		if (event.getComponent() == listeningParent) {
			// Scroll pane resized, resize ourselves aswell.
			resizeDesktop();
		} else {
			// We get a resize event instead of a shown event sometimes, so call
			// shown incase.
			componentShown(event);
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void componentHidden(final ComponentEvent event) {
		// If we are hidden, remove our listener
		if (event.getComponent() == this && listeningParent != null) {
			listeningParent.removeComponentListener(this);
			listeningParent = null;
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void componentShown(final ComponentEvent event) {
		// If we are shown, add our listener
		if (event.getComponent() == this && listeningParent == null) {
			if (getParent() != null) {
				listeningParent = getParent();
				listeningParent.addComponentListener(this);
			}
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void componentMoved(final ComponentEvent event) { /* Do Nothing */ }
	
	/** {@inheritDoc} */
	@Override
	public void setBounds(final int x, final int y, final int w, final int h) {
		super.setBounds(x,y,w,h);
		checkDesktopSize();
	}
	
	/** {@inheritDoc} */
	@Override
	public void remove(final Component c) {
		super.remove(c);
		checkDesktopSize();
	}
		
	/**
	 * Handles the maximizing of a frame.
	 * This resizes the desktop before maximizing a Frame so that it doesn't
	 * scroll, this also removes the northpane and borders.
	 *
	 * @param Frame Frame to maximise
	 */
	private void handleMaximizeFrame(final JInternalFrame frame) {
		if (getParent() instanceof JViewport) {
			final JViewport viewport = ((JViewport)getParent());
			final Container container = (viewport.getParent() != null) ? viewport.getParent() : viewport;
			final Dimension dimension = (container instanceof JComponent) ? ((JComponent)container).getVisibleRect().getSize() : container.getSize();
			// Accomodate borders
			final Insets insets = container.getInsets();
			dimension.setSize(dimension.getWidth() - insets.left - insets.right, dimension.getHeight() - insets.top - insets.bottom);
			
			setMinimumSize(dimension);
			setMaximumSize(dimension);
			setPreferredSize(dimension);
			
			viewport.invalidate();
			viewport.validate();
		}
		
		if (properlyMaximise && frame.getUI() instanceof BasicInternalFrameUI) {
			final BasicInternalFrameUI ui = (BasicInternalFrameUI)frame.getUI();
			
			// Remove Borders and titlebar
			ui.setNorthPane(null);
			frame.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		}
	}
	
	/**
	 * Handles the minimizing of a frame.
	 * This adds the northpane and borders back, also unmaximizes all other
	 * maximised frames.
	 * Based on code from DMDirc: http://fisheye.dmdirc.com/browse/DMDIrc/trunk/src/com/dmdirc/ui/swing/components/TextFrame.java?r=4523#l369
	 *
	 * @param Frame Frame to minimise
	 */
	private void handleMinimizeFrame(final JInternalFrame frame) {
		if (properlyMaximise && frame.getUI() instanceof BasicInternalFrameUI) {
			// Readd titlebar and border
			Object ui = null;
			final String componentUI = (String) UIManager.get("InternalFrameUI");
			if ("javax.swing.plaf.synth.SynthLookAndFeel".equals(componentUI)) {
				ui = SynthLookAndFeel.createUI(frame);
			} else {
				try {
					final Class<?> c = getClass().getClassLoader().loadClass(componentUI);
					final Constructor<?> constructor = c.getConstructor(new Class[]{JInternalFrame.class});
					ui = constructor.newInstance(new Object[]{frame});
				} catch (Exception ex) { System.err.println("Error: "+ex.getMessage()); }
			}
			if (ui == null) { ui = new BasicInternalFrameUI(frame); }
			
			frame.setBorder(UIManager.getBorder("InternalFrame.border"));
			frame.setUI((BasicInternalFrameUI) ui);
		}
		
		// Minimise all frames
		synchronized (this) {
			if (isResizing) { return; }
			else { isResizing = true; }
		}
		final JInternalFrame[] internalFrames = getAllFrames();
		for (JInternalFrame internalFrame : internalFrames) {
			try {
				internalFrame.setMaximum(false);
			} catch (PropertyVetoException pve) { }
		}
		try {
			frame.setSelected(true);
		} catch (PropertyVetoException pve) { }
		synchronized (this) { isResizing = false; }
	}

	/**
	 * Resize the desktop to accomodate child frames.
	 * Also moves Iconified frames to within the new bounds, and prevents anything
	 * being located in a negative x/y
	 */
	private void resizeDesktop() {
		if (getParent() instanceof JViewport) {
			synchronized (this) {
				if (isResizing) { return; }
				else { isResizing = true; }
			}
			final JViewport viewport = ((JViewport)getParent());
			
			int requiredWidth = 0;
			int requiredHeight = 0;
			final JInternalFrame[] internalFrames = getAllFrames();
			final ArrayList<JInternalFrame> iconifiedFrames = new ArrayList<JInternalFrame>();
			for (JInternalFrame frame : internalFrames) {
				if (frame.isIcon()) {
					iconifiedFrames.add(frame);
				} else {
					// Check for negative X/Y first, and move if needed.
					if (frame.getX() < 0 || frame.getY() < 0) {
						final int x = (frame.getX() < 0) ? 0 : frame.getX();
						final int y = (frame.getY() < 0) ? 0 : frame.getY();
						frame.reshape(x, y, frame.getWidth(), frame.getHeight());
					}
					
					if (frame.getX() + frame.getWidth() > requiredWidth) {
						requiredWidth = frame.getX() + frame.getWidth();
					}
					if (frame.getY() + frame.getHeight() > requiredHeight) {
						requiredHeight = frame.getY() + frame.getHeight();
					}
					
					// If we have a maximised frame, resize to container size and exit
					if (frame.isMaximum()) {
						handleMaximizeFrame(frame);
						synchronized (this) { isResizing = false; }
						return;
					}
				}
			}
			
			// Resize to the viewports parent if it has one.
			final Container container = (viewport.getParent() != null) ? viewport.getParent() : viewport;
			final Dimension dimension = (container instanceof JComponent) ? ((JComponent)container).getVisibleRect().getSize() : container.getSize();
			
			// Accomodate borders
			final Insets insets = container.getInsets();
			dimension.setSize(dimension.getWidth() - insets.left - insets.right, dimension.getHeight() - insets.top - insets.bottom);
			
			if (requiredWidth <= dimension.getWidth()) { requiredWidth = (int)dimension.getWidth(); }
			if (requiredHeight <= dimension.getHeight()) { requiredHeight = (int)dimension.getHeight(); }
			
			// Make the viewport show the right size for the desktop
			setMinimumSize(new Dimension(requiredWidth, requiredHeight));
			setMaximumSize(new Dimension(requiredWidth, requiredHeight));
			setPreferredSize(new Dimension(requiredWidth, requiredHeight));
			viewport.invalidate();
			viewport.validate();
			
			// Fix iconified frames positions, move them within the view of the
			// viewports parent.
			// In the case of a JScrollPane this means that the icons are resized to
			// be level with the bottom of the scrollpane rather than the viewport
			// (and thus may be covered by scrollbars)
			for (JInternalFrame frame : iconifiedFrames) {
				final JDesktopIcon desktopIcon = frame.getDesktopIcon();
				final Rectangle bounds = desktopIcon.getBounds();
				
				// Get the x/y position this should be to remain in the frame.
				int x = (bounds.getX() + bounds.getWidth() > requiredWidth) ? requiredWidth - (int)bounds.getWidth() : (int)bounds.getX();
				int y = (bounds.getY() + bounds.getHeight() > requiredHeight) ? requiredHeight - (int)bounds.getHeight() : (int)bounds.getY();
				// Check for negatives aswell.
				if (x < 0) { x = 0; }
				if (y < 0) { y = 0; }
				
				desktopIcon.setBounds(x, y, desktopIcon.getWidth(), desktopIcon.getHeight());
			}
			synchronized (this) { isResizing = false; }
		}
	}
	
	/**
	 * Check that our size is what it should be.
	 */
	private void checkDesktopSize() {
		if (getParent() != null && isVisible()) {
			resizeDesktop();
		}
	}
}