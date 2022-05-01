package it.unibas.mythesis;

import it.unibas.mythesis.view.DBManagementPanel;
import it.unibas.mythesis.view.MetadataTablePanel;
import it.unibas.mythesis.view.FrameView;
import it.unibas.mythesis.view.SummaryPanel;
import it.unibas.mythesis.view.TesseractPanel;
import it.unibas.mythesis.view.MainPanel;
import it.unibas.mythesis.view.MetadataPanel;
import it.unibas.mythesis.backend.DesktopBackend;
import it.unibas.mythesis.control.MenuControl;
import it.unibas.mythesis.control.MetadataPanelControl;
import it.unibas.mythesis.control.MainPanelControl;
import it.unibas.mythesis.control.DBManagementPanelControl;
import it.unibas.mythesis.control.TesseractPanelControl;
import it.unibas.mythesis.model.Model;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application{

	private static Application instance = new Application();

	//MODEL
	private Model model;
	//VIEW
	private FrameView frameView;
	private MainPanel mainPanel;
	private MetadataPanel metadataPanel;
	private MetadataTablePanel metadataTablePanel;
	private DBManagementPanel dbManagementPanel;
	private TesseractPanel tesseractPanel;
	private SummaryPanel summaryPanel;
	//CONTROL
	private MenuControl menuControl;
	private MainPanelControl mainPanelControl;
	private MetadataPanelControl metadataPanelControl;
	private DBManagementPanelControl dbManagementPanelControl;
	private TesseractPanelControl tesseractPanelControl;
	//BACKEND
	private DesktopBackend desktopBackend;

	public static Application getInstance() {
		return instance;
	}

	private void initialize() {
		//BACKEND
		this.desktopBackend = new DesktopBackend();
		//MODEL
		this.model = new Model();
		//CONTROL
		this.menuControl = new MenuControl();
		this.mainPanelControl = new MainPanelControl();
		this.metadataPanelControl = new MetadataPanelControl();
		this.dbManagementPanelControl = new DBManagementPanelControl();
		this.tesseractPanelControl = new TesseractPanelControl();
		//VIEW
		this.frameView = new FrameView();
		this.mainPanel = new MainPanel();
		this.metadataPanel = new MetadataPanel(frameView);
		this.metadataTablePanel = new MetadataTablePanel(frameView);
		this.dbManagementPanel = new DBManagementPanel(frameView);
		this.tesseractPanel = new TesseractPanel(frameView);
		this.summaryPanel = new SummaryPanel(frameView);
		//initialize
		this.summaryPanel.initialize();
		this.metadataTablePanel.initialize();
		this.tesseractPanel.initialize();
		this.metadataPanel.initialize();
		this.dbManagementPanel.initialize();
		this.mainPanel.initialize();
		this.frameView.initialize();
	}

	public DesktopBackend getDesktopBackend() {
		return desktopBackend;
	}

	public Model getModel() {
		return model;
	}

	public TesseractPanelControl getTesseractPanelControl() {
		return tesseractPanelControl;
	}

	public DBManagementPanelControl getDBManagementPanelControl() {
		return dbManagementPanelControl;
	}

	public MetadataPanelControl getMetadataPanelControl() {
		return metadataPanelControl;
	}

	public MainPanelControl getMainPanelControl() {
		return mainPanelControl;
	}

	public MenuControl getMenuControl() {
		return menuControl;
	}

	public MetadataTablePanel getMetadataTablePanel() {
		return metadataTablePanel;
	}

	public DBManagementPanel getDBManagementPanel() {
		return dbManagementPanel;
	}

	public MetadataPanel getMetadataPanel() {
		return metadataPanel;
	}

	public FrameView getFrameView() {
		return frameView;
	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}

	public TesseractPanel getTesseractPanel() {
		return tesseractPanel;
	}

	public SummaryPanel getSummaryPanel() {
		return summaryPanel;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		System.setProperty("java.awt.headless", "false");
		Application.getInstance().initialize();
	}

}
