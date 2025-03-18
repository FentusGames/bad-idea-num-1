package core.screens;

import static jooq.generated.tables.Genes.GENES;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import core.Core;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTableColumnFlags;

public class ScreenGame extends Screen {

	private String originalGene;
	private StringBuilder currentGene;
	private String targetGene;
	private int movesLeft;
	private int score;

	private List<Character> nextGenes;

	private String geneDescription;
	private String geneDiseases;
	private boolean showFailurePopup = false;

	public ScreenGame(Core core) {
		super(core);
	}

	@Override
	public void init() {
		super.init();
		initGame();
	}

	private void initGame() {
		DSLContext db = core.getDB();

		var geneRecord = db.selectFrom(GENES).orderBy(DSL.rand()).limit(1).fetchOne();
		if (geneRecord != null) {
			originalGene = geneRecord.getEncodedsymbol();
			geneDescription = geneRecord.getDescription();
			geneDiseases = geneRecord.getDiseases();
		} else {
			originalGene = "ACACAACTGGCGGACGGCCA";
			geneDescription = "Unknown gene description.";
			geneDiseases = "";
		}

		currentGene = new StringBuilder(scrambleGene(originalGene));
		targetGene = originalGene;
		movesLeft = 15;
		score = 0;
		nextGenes = generateNextGenes();
		showFailurePopup = false;
	}

	private String scrambleGene(String gene) {
		if (gene == null || gene.isEmpty()) {
			return "";
		}
		char[] chars = gene.toCharArray();
		Random rand = new Random();
		for (int i = 0; i < chars.length; i++) {
			int swapIdx = rand.nextInt(chars.length);
			char temp = chars[i];
			chars[i] = chars[swapIdx];
			chars[swapIdx] = temp;
		}
		return new String(chars);
	}

	private List<Character> generateNextGenes() {
		List<Character> list = new LinkedList<>();
		Random rand = new Random();
		char[] bases = { 'A', 'T', 'C', 'G' };
		for (int i = 0; i < 5; i++) {
			list.add(bases[rand.nextInt(bases.length)]);
		}
		return list;
	}

	@Override
	public void update(float delta) {
		if (movesLeft <= 0 && !currentGene.toString().equals(targetGene)) {
			showFailurePopup = true;
		}
	}

	@Override
	public void imgui(float delta, int windowX, int windowY, int windowWidth, int windowHeight) {
		if (showFailurePopup) {
			ImGui.openPopup("Gene Failure");
		}

		ImGui.begin("Gene Editor");

		ImGui.text("Fix the DNA sequence to match the target!");
		ImGui.text("Moves Left: " + movesLeft);
		ImGui.text("Score: " + score);

		float matchPercentage = computeMatchPercentage();
		ImGui.text(String.format("Match: %.2f%%", matchPercentage));

		ImGui.text("Target Gene:");
		drawTargetGeneTable();

		ImGui.text("Current Gene:");
		drawCurrentGeneTable();

		ImGui.text("Insertable Genes:");
		for (int i = 0; i < nextGenes.size(); i++) {
			ImGui.sameLine();
			drawNextGeneDragSource(nextGenes.get(i), i);
		}

		if (currentGene.toString().equals(targetGene)) {
			ImGui.text("Success! You fixed the gene.");
			score += 100;
			if (ImGui.button("Play Again")) {
				initGame();
			}
		}

		ImGui.end();

		if (ImGui.beginPopupModal("Gene Failure")) {
			ImGui.text("You failed to match the gene!");
			ImGui.separator();
			ImGui.text("Description: " + (geneDescription != null ? geneDescription : "N/A"));
			if (geneDiseases != null && !geneDiseases.isEmpty()) {
				ImGui.text("Diseases: " + geneDiseases);
			} else {
				ImGui.text("Diseases: None");
			}
			ImGui.separator();

			if (ImGui.button("OK")) {
				ImGui.closeCurrentPopup();
				initGame();
			}
			ImGui.endPopup();
		}
	}

	private void drawTargetGeneTable() {
		if (targetGene == null || targetGene.isEmpty()) {
			ImGui.text("[Empty]");
			return;
		}
		int length = targetGene.length();
		if (length == 0) {
			ImGui.text("[Empty]");
			return;
		}

		if (ImGui.beginTable("TargetGeneTable", length)) {
			for (int col = 0; col < length; col++) {
				ImGui.tableSetupColumn("TargetCol" + col, ImGuiTableColumnFlags.WidthFixed, 50f);
			}

			ImGui.tableNextRow();

			for (int i = 0; i < length; i++) {
				ImGui.tableSetColumnIndex(i);
				String text = String.valueOf(targetGene.charAt(i));
				ImGui.text(text);
			}
			ImGui.endTable();
		}
	}

	private void drawCurrentGeneTable() {
		if (currentGene == null || currentGene.length() == 0) {
			ImGui.text("[Empty]");
			return;
		}
		int length = currentGene.length();
		if (length == 0) {
			ImGui.text("[Empty]");
			return;
		}

		if (ImGui.beginTable("CurrentGeneTable", length)) {
			for (int col = 0; col < length; col++) {
				ImGui.tableSetupColumn("CurrentCol" + col, ImGuiTableColumnFlags.WidthFixed, 50f);
			}

			ImGui.tableNextRow();

			for (int i = 0; i < length; i++) {
				ImGui.tableSetColumnIndex(i);
				boolean isCorrect = (i < targetGene.length() && currentGene.charAt(i) == targetGene.charAt(i));

				if (isCorrect) {
					ImGui.pushStyleColor(ImGuiCol.Button, ImColor.rgba(0.0f, 0.5f, 0.0f, 1.0f));
					ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.rgba(0.0f, 0.7f, 0.0f, 1.0f));
				} else {
					ImGui.pushStyleColor(ImGuiCol.Button, ImColor.rgba(0.5f, 0.0f, 0.0f, 1.0f));
					ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.rgba(0.7f, 0.0f, 0.0f, 1.0f));
				}

				String label = currentGene.charAt(i) + "##" + i;
				float buttonWidth = 45f;
				float buttonHeight = 25f;
				ImGui.button(label, buttonWidth, buttonHeight);
				ImGui.popStyleColor(2);

				if (ImGui.beginDragDropSource()) {
					ImGui.setDragDropPayload("CELL_INDEX", i, ImGuiCond.Once);
					ImGui.text("Dragging cell " + i);
					ImGui.endDragDropSource();
				}

				if (ImGui.beginDragDropTarget()) {
					Object cellPayload = ImGui.acceptDragDropPayload("CELL_INDEX");
					if (cellPayload != null) {
						int fromIndex = (Integer) cellPayload;
						if (Math.abs(fromIndex - i) == 1) {
							swap(fromIndex, i);
						}
					}

					Object nextPayload = ImGui.acceptDragDropPayload("NEXT_CHAR");
					if (nextPayload != null) {
						int nextIndex = (Integer) nextPayload;
						if (nextIndex >= 0 && nextIndex < nextGenes.size()) {
							char c = nextGenes.get(nextIndex);
							replaceGene(i, c);

							nextGenes.remove(nextIndex);
							Random rand = new Random();
							char[] bases = { 'A', 'T', 'C', 'G' };
							nextGenes.add(bases[rand.nextInt(bases.length)]);
						}
					}
					ImGui.endDragDropTarget();
				}
			}
			ImGui.endTable();
		}
	}

	private void drawNextGeneDragSource(Character c, int index) {
		ImGui.pushID(index);
		ImGui.button(String.valueOf(c));
		if (ImGui.beginDragDropSource()) {
			ImGui.setDragDropPayload("NEXT_CHAR", index, ImGuiCond.Once);
			ImGui.text("Replacing with: " + c);
			ImGui.endDragDropSource();
		}
		ImGui.popID();
	}

	private float computeMatchPercentage() {
		if (targetGene == null || targetGene.isEmpty())
			return 0f;
		if (currentGene == null || currentGene.length() == 0)
			return 0f;

		int matchCount = 0;
		int length = Math.min(currentGene.length(), targetGene.length());
		for (int i = 0; i < length; i++) {
			if (currentGene.charAt(i) == targetGene.charAt(i)) {
				matchCount++;
			}
		}
		return (matchCount / (float) targetGene.length()) * 100f;
	}

	private void swap(int i, int j) {
		if (i < 0 || j < 0 || i >= currentGene.length() || j >= currentGene.length()) {
			return;
		}
		char temp = currentGene.charAt(i);
		currentGene.setCharAt(i, currentGene.charAt(j));
		currentGene.setCharAt(j, temp);
		movesLeft--;
	}

	private void replaceGene(int index, char nucleotide) {
		if (index >= 0 && index < currentGene.length()) {
			currentGene.setCharAt(index, nucleotide);
			movesLeft--;
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public String getMenuName() {
		return core.getLanguage("play");
	}

	@Override
	public void scroll(long window, double xoffset, double yoffset) {

	}

	@Override
	public void mouseButton(long window, int button, int action, int mods) {

	}

	@Override
	public void key(long window, int key, int scancode, int action, int mods) {

	}

	@Override
	public void render(float delta) {

	}

	@Override
	protected int getOrder() {
		return 0;
	}
}