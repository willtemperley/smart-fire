package it.jrc.smart.fire.job;

import org.eclipse.core.runtime.IProgressMonitor;

final class MockProgressMonitor implements IProgressMonitor {
	@Override
	public void worked(int work) {
		System.out.println("WORKED: " + work);
	}

	@Override
	public void subTask(String name) {
	}

	@Override
	public void setTaskName(String name) {
	}

	@Override
	public void setCanceled(boolean value) {
	}

	@Override
	public boolean isCanceled() {
		return false;
	}

	@Override
	public void internalWorked(double work) {
	}

	@Override
	public void done() {
	}

	@Override
	public void beginTask(String name, int totalWork) {
	}
}