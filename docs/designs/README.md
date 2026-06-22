# designs/

Folder ini berisi semua aset desain untuk aplikasi MaterialKu.

## File

| File | Deskripsi |
|---|---|
| `design_system.md` | Design system lengkap: color palette, typography, spacing, komponen UI, wireframe ASCII untuk 9 screen, reusable components, animasi, accessibility |
| `mockup.pen` | Mockup visual interaktif yang bisa dibuka dengan Pencil Editor (pencil.software). Berisi 9 screen + komponen reusable |

## 9 Screen

1. Login — Dark immersive, logo amber, icon-prefixed inputs
2. Dashboard — Hero stats (3 dark cards) + 2-col menu grid
3. Material List — Filter chips + cards with status badges (AMAN/KRITIS)
4. Material Form — Icon-prefixed text fields + dropdowns
5. POS — Product cards with add buttons + sticky checkout bar
6. Stok List — Big stock numbers + mutasi buttons
7. Laporan — Summary cards + transaction list + kritis section
8. User Management — Avatar initials + role badges
9. Settings — Profile card + dark mode toggle + about rows

## Cara Buka mockup.pen

1. Install Pencil Editor dari https://pencil.software
2. Buka file `mockup.pen`
3. Klik tiap screen untuk melihat detail layout

## Design System Highlights

- **Primary**: Amber `#F59E0B` (industrial warmth)
- **Background Dark**: `#1A1D21` (login, profile cards)
- **Background Light**: `#F7F8FA`
- **Typography**: Inter, weight 600-800 for emphasis
- **Cards**: 14-16dp radius, 1dp stroke
- **Buttons**: Pill shape (24-27dp radius)
- **Badges**: Color-coded by role/status, uppercase
