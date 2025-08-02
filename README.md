# 🗨️ WeirdChat – Mod Minecraft RP

![Forge 1.16.5](https://img.shields.io/badge/Forge-1.16.5-blue)
![Status: WIP](https://img.shields.io/badge/Status-Work_in_Progress-yellow)
![License: MIT](https://img.shields.io/badge/License-MIT-green)

> Un mod Forge 1.16.5 pour un système de messagerie RP intégré, inspiré de Twitter / WeChat, avec gestion de followers, messages publics et privés, et affichage stylisé côté client.

---

## ✨ Fonctionnalités principales

- 💬 Commande `/wechat` pour publier des messages publics
- 👤 Système de followers : abonnez-vous à d'autres joueurs RP
- 📬 Messagerie privée avec `/whisper`
- 🧼 Commande `/wechattoggle` pour désactiver temporairement la réception
- 🖼️ Overlay client custom avec affichage dans le HUD
- 🌐 Intégration Discord (webhook facultatif)

---

## 📁 Structure du mod

- `commands/` → `/wechat`, `/follow`, `/info`, etc.
- `storage/` → Système de comptes, pseudos RP, followers
- `network/` → Synchro client/serveur des messages
- `render/` → Affichage client (ChatBox overlay)
- `util/` → Outils internes
- `WebhookSender.java` → Optionnel, désactivé par défaut

---

## 🧪 Dépendances

- Forge 1.16.5
- Gradle
- (Optionnel) Webhook Discord

---

## 📄 Licence

- Distribué sous licence MIT. Voir [LICENSE](LICENSE).
+ Ce projet est protégé par droits d’auteur. Voir [LICENSE](LICENSE).
