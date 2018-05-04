package animation2;

import ij.ImagePlus;
import ij.measure.Calibration;
import renderer3d.ExtendedRenderingState;
import renderer3d.OpenCLRaycaster;
import renderer3d.Renderer3D;

public class RenderingThread {

	private Thread thread;
	private boolean shutdown = false;

	private Renderer3D raycaster;

	final ImagePlus out;

	private final Event event;

	static class Event {

		private ExtendedRenderingState rs;
		private boolean forceUpdateProgram = false;
		private int tgtW = -1;
		private int tgtH = -1;
		private boolean valid;

		Event(ExtendedRenderingState rs) {
			valid = true;
			this.rs = rs.clone();
		}
	}

	public RenderingThread(Renderer3D raycaster) {
		this.raycaster = raycaster;
		final ExtendedRenderingState rs = raycaster.getRenderingState();
		rs.setNonChannelProperty(ExtendedRenderingState.TIMEPOINT, raycaster.getImage().getT() - 1);
		this.event = new Event(rs);
		out = new ImagePlus("3D Animation", raycaster.render(rs));
		// TODO
		Calibration cal = out.getCalibration();
		cal.setUnit(raycaster.getImage().getCalibration().getUnit());
		rs.getFwdTransform().adjustOutputCalibration(cal);
		out.show();

		thread = new Thread("3D-Animation rendering thread") {
			@Override
			public void run() {
				loop(rs);
			}
		};
		thread.start();
	}

	public void loop(ExtendedRenderingState rs) {
		Event e = new Event(rs);
		while(!shutdown) {
			poll(e);
			for(int dz = 5; dz >= 1; dz--) {
				e.rs.getFwdTransform().setZStep(dz);
				render(e);
				if(event.valid)
					break;
			}
		}
		OpenCLRaycaster.close();
	}

	public synchronized void push(ExtendedRenderingState rs, int w, int h, boolean forceUpdateProgram) {
		event.rs.setFrom(rs);
		event.valid = true;
		event.tgtW = w;
		event.tgtH = h;
		event.forceUpdateProgram = forceUpdateProgram;
		notifyAll();
	}

	public synchronized Event poll(Event ret) {
		while(!event.valid && !shutdown) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ret.rs.setFrom(event.rs);
		ret.tgtW = event.tgtW;
		ret.tgtH = event.tgtH;
		ret.forceUpdateProgram = event.forceUpdateProgram;
		event.valid = false;
		return ret;
	}

	public void shutdown() {
		shutdown = true;
		synchronized(this) {
			notifyAll();
		}
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void render(Event e) {
		if(e.tgtW != -1 && e.tgtH != -1) {
			raycaster.setTgtSize(e.tgtW, e.tgtH);
			e.tgtW = e.tgtH = -1;
		}
		out.setProcessor(raycaster.render(e.rs, e.forceUpdateProgram));
		e.forceUpdateProgram = false;
	}
}