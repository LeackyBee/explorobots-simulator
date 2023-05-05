package io.github.leackybee.views.simulator;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import io.github.leackybee.simulator.SimulationCycle;
import io.github.leackybee.views.MainLayout;

@PageTitle("Simulator")
@Route(value = "", layout = MainLayout.class)
public class SimulatorView extends VerticalLayout {

    private FeederThread thread;
    Image canvas = new Image("images/canvas.png", "");

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        thread = new FeederThread(canvas.getElement().getComponent().get().getUI().get(), this);
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        thread.interrupt();
        thread = null;
    }

    public SimulatorView() {
        setSpacing(false);

        canvas.setWidth("1000px");
        add(canvas);

        /**H2 header = new H2("This place intentionally left empty");
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        add(header);
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
        **/
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    private static class FeederThread extends Thread {
        private final UI ui;
        private final SimulatorView view;
        private SimulationCycle simulator;


        public FeederThread(UI ui, SimulatorView view) {
            this.ui = ui;
            this.view = view;
            this.simulator = new SimulationCycle();
        }

        int count = 0;

        @Override
        public void run() {
            // Update the data for a while
            while(count < 100){
                simulator.start(1);
                ui.access(()->{
                    view.canvas.setSrc("images/canvas" + simulator.getTimestep() + ".png");
                    view.canvas.setAlt("Timestep = " + simulator.getTimestep());
                    ui.push();
                });
                count++;
            }
        }
    }

}


