package bah.tahi.morpion.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bah.tahi.morpion.Owner;
import bah.tahi.morpion.TicTacToeModel;

public class TestMorpion {

	/**
	 * Instance du modèle de jeu.
	 */
	private final TicTacToeModel morpions = TicTacToeModel.getInstance();
	private final int NB_CASES = morpions.getNbCases();

	@BeforeEach
	public void setUp() {
		morpions.restart();
	}

	/**
	 * Fonction pour tester l'état initial de la partie.
	 */
	@Test
	public void testInit() {
		testEmptyGrid();
		assertEquals(Owner.FIRST, morpions.turnProperty().get(), "Le premier doit jouer");
		assertEquals(Owner.NONE, morpions.winnerProperty().get(),
				"Il ne doit pas y avoir de gagnant au début de la partie");
		assertEquals(0, morpions.getScore(Owner.FIRST).intValue(),
				"Il doit y avoir 0 coup joué par le premier joueur au début de la partie");
		assertEquals(0, morpions.getScore(Owner.SECOND).intValue(),
				"Il doit y avoir 0 coup joué par le deuxième joueur au début de la partie");
		assertTrue(!morpions.gameOver().get(), "La partie ne doit pas être terminée au debut");
		testInvariant();
	}

	/**
	 * Fonction à utiliser après chaque action, pour tester les conditions qui
	 * doivent toujours être vraies.
	 */
	private void testInvariant() {
		int firstScore = morpions.getScore(Owner.FIRST).intValue();
		int secondScore = morpions.getScore(Owner.SECOND).intValue();

		assertTrue(firstScore >= 0, "Nombre de coups du premier joueur >= 0");
		assertTrue(firstScore <= NB_CASES, "Nombre de coups du premier joueur <= " + NB_CASES);
		assertTrue(secondScore >= 0, "Nombre de coups du premier joueur >= 0");
		assertTrue(secondScore <= NB_CASES, "Nombre de coups du premier joueur <= " + NB_CASES);
		assertNotEquals(Owner.NONE, morpions.turnProperty().get(), "Il doit avoir un joueur après chaque action");
	}

	/**
	 * Fonction servant à tester si toutes les cases du plateau sont vides.
	 */
	private void testEmptyGrid() {
		for (int i = 0; i < morpions.getBoardHeight(); i++) {
			for (int j = 0; j < morpions.getBoardWidth(); j++) {
				assertEquals(Owner.NONE, morpions.getSquare(i, j).get(),
						"La case (" + i + ", " + j + ") doit être vide");
				assertTrue(morpions.legalMove(i, j).get(), "La case (" + i + ", " + j + ") n'est pas libre");
			}
		}
	}

	/**
	 * Fonction pour tester l'état de la partie après un restart.
	 */
	@Test
	public void testRestart() {
		// On entamme une partie
		morpions.play(0, 0);
		assertEquals(Owner.FIRST, morpions.getSquare(0, 0).get());

		morpions.play(1, 1);
		assertEquals(Owner.SECOND, morpions.getSquare(1, 1).get());

		// On recommence la partie
		morpions.restart();

		// Et ensuite on test l'état de la partie
		testInit();
	}

	/**
	 * Fonction vérifiant le bon fonctionnement de getTurn()
	 */
	@Test
	public void testGetJoueur() {
		assertEquals(Owner.FIRST, morpions.turnProperty().get(),
				"Dès le début du jeu, ça doit être au premier joueur de jouer");
		morpions.nextPlayer();

		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				assertEquals(Owner.SECOND, morpions.turnProperty().get(), "ça doit être au deuxième joueur de jouer");
			} else {
				assertEquals(Owner.FIRST, morpions.turnProperty().get(), "ça doit être au premier joueur de jouer");
			}
			morpions.nextPlayer();
		}
	}

	/**
	 * Fonction vérifiant le bon fonctionnement de getSquare()
	 */
	@Test
	public void testGetSquare() {
		assertEquals(Owner.NONE, morpions.getSquare(0, 0).get(), "La case doit être vide");
		morpions.play(0, 0);
		assertEquals(Owner.FIRST, morpions.getSquare(0, 0).get(), "La case doit appartenir au premier joueur");

		assertEquals(Owner.NONE, morpions.getSquare(0, 1).get(), "La case doit être vide");
		morpions.play(0, 1);
		assertEquals(Owner.SECOND, morpions.getSquare(0, 1).get(), "La case doit appartenir au second joueur");
	}

	/**
	 * Fonction vérifiant le bon fonctionnement de validSquare()
	 */
	@Test
	public void testValidSquare() {
		assertTrue(morpions.validSquare(0, 0), "Coordonnées de case correctes");
		assertTrue(morpions.validSquare(1, 1), "Coordonnées de case correctes");
		assertTrue(morpions.validSquare(2, 2), "Coordonnées de case correctes");

		assertTrue(!morpions.validSquare(3, 0), "Coordonnées de case incorrectes");
		assertTrue(!morpions.validSquare(0, 3), "Coordonnées de case incorrectes");
		assertTrue(!morpions.validSquare(-1, 0), "Coordonnées de case incorrectes");
		assertTrue(!morpions.validSquare(0, -1), "Coordonnées de case incorrectes");
	}

	/**
	 * Fonction pour tester un cas de victoire avec des cases libres
	 */
	@Test
	public void testVictoire1() { // OK
		// FIRST joue dans la case (0,0)
		assertTrue(morpions.legalMove(0, 0).get());
		morpions.play(0, 0);
		testInvariant();

		// SECOND joue dans la case (1,1)
		assertTrue(morpions.legalMove(1, 1).get());
		morpions.play(1, 1);
		testInvariant();

		// FIRST joue dans la case (0,2)
		assertTrue(morpions.legalMove(0, 2).get());
		morpions.play(0, 2);
		testInvariant();

		// SECOND joue dans la case (2,1)
		assertTrue(morpions.legalMove(2, 1).get());
		morpions.play(2, 1);
		testInvariant();

		// FIRST joue dans la case (0,1)
		assertTrue(morpions.legalMove(0, 1).get());
		morpions.play(0, 1);
		testInvariant();

		// Vérifications
		assertEquals(3, morpions.getScore(Owner.FIRST).intValue(),
				"Le nombre de coups joués par le premier joueur est incorrect");
		assertEquals(2, morpions.getScore(Owner.SECOND).intValue(),
				"Le nombre de coups joués par le deuxième joueur est incorrect");
		assertEquals(Owner.FIRST, morpions.winnerProperty().get(), "FIRST n'est pas le vainqueur");
		assertTrue(morpions.gameOver().get(), "La partie doit être terminée");
	}

	/**
	 * Fonction pour tester un cas de victoire à la dernière case
	 */
	@Test
	public void testVictoire2() {
		testEmptyGrid();

		// FIRST joue dans la case (0,0)
		assertTrue(morpions.legalMove(0, 0).get());
		morpions.play(0, 0);
		testInvariant();

		// SECOND joue dans la case (0,1)
		assertTrue(morpions.legalMove(0, 1).get());
		morpions.play(0, 1);
		testInvariant();

		// FIRST joue dans la case (2,1)
		assertTrue(morpions.legalMove(2, 1).get());
		morpions.play(2, 1);
		testInvariant();

		// SECOND joue dans la case (1,2)
		assertTrue(morpions.legalMove(1, 2).get());
		morpions.play(1, 2);
		testInvariant();

		// FIRST joue dans la case (1,0)
		assertTrue(morpions.legalMove(1, 0).get());
		morpions.play(1, 0);
		testInvariant();

		// SECOND joue dans la case (2,2)
		assertTrue(morpions.legalMove(2, 2).get());
		morpions.play(2, 2);
		testInvariant();

		// FIRST joue dans la case (0, 2)
		assertTrue(morpions.legalMove(0, 2).get());
		morpions.play(0, 2);
		testInvariant();

		// SECOND joue dans la case (1,1)
		assertTrue(morpions.legalMove(1, 1).get());
		morpions.play(1, 1);
		testInvariant();

		// FIRST joue dans la case (2, 0)
		assertTrue(morpions.legalMove(2, 0).get());
		morpions.play(2, 0);
		testInvariant();

		// Vérifications
		assertEquals(5, morpions.getScore(Owner.FIRST).intValue(),
				"Le nombre de coups joués par le premier joueur est incorrect");
		assertEquals(4, morpions.getScore(Owner.SECOND).intValue(),
				"Le nombre de coups joués par le deuxième joueur est incorrect");
		assertEquals(Owner.FIRST, morpions.winnerProperty().get(), "FIRST doit être le vainqueur");
		assertTrue(morpions.gameOver().get(), "La partie doit être terminée");
	}

	/**
	 * Fonction pour tester un cas de match nul
	 */
	@Test
	public void testMatchNul() { // En cours
		// FIRST joue dans la case (2,2)
		assertTrue(morpions.legalMove(2, 2).get());
		morpions.play(2, 2);
		testInvariant();

		// SECOND joue dans la case (1,1)
		assertTrue(morpions.legalMove(1, 1).get());
		morpions.play(1, 1);
		testInvariant();

		// FIRST joue dans la case (2,1)
		assertTrue(morpions.legalMove(2, 1).get());
		morpions.play(2, 1);
		testInvariant();

		// SECOND joue dans la case (2,0)
		assertTrue(morpions.legalMove(2, 0).get());
		morpions.play(2, 0);
		testInvariant();

		// FIRST joue dans la case (0,2)
		assertTrue(morpions.legalMove(0, 2).get());
		morpions.play(0, 2);
		testInvariant();

		// SECOND joue dans la case (1,2)
		assertTrue(morpions.legalMove(1, 2).get());
		morpions.play(1, 2);
		testInvariant();

		// FIRST joue dans la case (1,0)
		assertTrue(morpions.legalMove(1, 0).get());
		morpions.play(1, 0);
		testInvariant();

		// SECOND joue dans la case (0,0)
		assertTrue(morpions.legalMove(0, 0).get());
		morpions.play(0, 0);
		testInvariant();

		// FIRST joue dans la case (0,1)
		assertTrue(morpions.legalMove(0, 1).get());
		morpions.play(0, 1);
		testInvariant();

		// Vérifications
		assertEquals(5, morpions.getScore(Owner.FIRST).intValue(),
				"Le nombre de coups joués par le premier joueur est incorrect");
		assertEquals(4, morpions.getScore(Owner.SECOND).intValue(),
				"Le nombre de coups joués par le deuxième joueur est incorrect");
		assertEquals(Owner.NONE, morpions.winnerProperty().get(), "Il ne doit pas y avoir de vainqueur");
		assertTrue(morpions.gameOver().get(), "La partie doit être terminée");
	}
}