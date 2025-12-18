package com.chesspong.model;

/**
 * Représente le pouvoir de la balle.
 * - capacite: nombre de touches nécessaires pour charger (ex: 5).
 * - power: budget de points de vie consommables quand activé (ex: 3).
 * - charge: progression actuelle vers la capacité.
 * - remaining: budget restant pendant l'activation.
 */
public class Pouvoir {
    private int capacite;     // ex: 5
    private int power;        // ex: 3 (budget total à consommer)
    private int charge;       // 0..capacite
    private int remaining;    // >0 quand actif

    public Pouvoir(int capacite) {
        this.capacite = Math.max(1, capacite);
        this.power = 0;
        this.charge = 0;
        this.remaining = 0;
    }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = Math.max(1, capacite); }

    public int getPower() { return power; }
    public void setPower(int power) { this.power = Math.max(0, power); }

    public int getCharge() { return charge; }

    public int getRemaining() { return remaining; }

    public boolean isReady() { return charge >= capacite && remaining == 0; }

    public boolean isActive() { return remaining > 0; }

    /** Incrémente la charge (si non actif). Retourne true si la jauge est pleine. */
    public boolean incrementCharge() {
        if (isActive()) return false; // on ne (re)charge pas pendant l'activation
        if (charge < capacite) charge++;
        return charge >= capacite;
    }

    /** Active le pouvoir en consommant la charge, si un power > 0 a été défini. */
    public boolean activateIfReady() {
        if (isReady() && power > 0) {
            this.remaining = power;
            this.charge = 0; // on vide la jauge pour la prochaine charge
            return true;
        }
        return false;
    }

    /** Consomme 1 point lors d'un contact avec le bord haut/bas. */
    public void consumeOnBoundary() {
        if (remaining > 0) remaining--;
    }

    /** Consomme un certain montant (habituellement la vie de la pièce). Retourne la quantité réellement consommée. */
    public int consume(int amount) {
        if (remaining <= 0 || amount <= 0) return 0;
        int used = Math.min(remaining, amount);
        remaining -= used;
        return used;
    }

    /** Désactive explicitement */
    public void resetActivation() {
        this.remaining = 0;
    }
}

