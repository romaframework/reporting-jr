package org.romaframework.aspect.reporting.jr.view.domain;

import org.romaframework.aspect.flow.annotation.FlowAction;
import org.romaframework.core.Roma;
import org.romaframework.frontend.view.domain.RomaControlPanelTab;

public class ReportingControlPanel implements RomaControlPanelTab {
	@FlowAction(next = TemplateManagerMain.class, position = "body")
	public void templateManager() {
		Roma.flow().forward(new TemplateManagerMain(), "screen://body");
	}

}
