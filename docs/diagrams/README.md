# Diagrammes UML (Mermaid)

Diagrammes du système StreetLeague, au format [Mermaid](https://mermaid.js.org/).

| Fichier | Diagramme |
|---|---|
| `class-diagram.mmd` | Diagramme de classes — modèle de domaine (Event, Match, Statistiques, Classement, FeuillesDeMatch) et leurs relations. |
| `sequence-match-completion.mmd` | Diagramme de séquence — création/finalisation d'un match et cascade vers stats, classement et feuille de match. |
| `use-case-diagram.mmd` | Diagramme de cas d'utilisation — actions de l'organisateur. |

## Visualiser

- Coller le contenu d'un `.mmd` dans <https://mermaid.live>, **ou**
- Utiliser l'aperçu Mermaid de VS Code / GitHub (rendu automatique des blocs Mermaid), **ou**
- Générer une image :

```bash
npx @mermaid-js/mermaid-cli -i class-diagram.mmd -o class-diagram.svg
```
