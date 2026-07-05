# AgriHub Platform - Multi-Language Configuration

## Supported Languages

| Code | Language | Countries | Status |
|------|----------|-----------|--------|
| en | English | MW, ZM, KE, RW | Primary |
| ny | Chichewa | MW | Complete |
| sw | Swahili | TZ, KE | Complete |
| fr | French | RW, CD | Complete |
| pt | Portuguese | MZ | Complete |
| bem | Bemba | ZM | Complete |

## Country Language Mapping

| Country | Primary | Secondary | Tertiary |
|---------|---------|-----------|----------|
| Malawi (MW) | English | Chichewa | - |
| Zambia (ZM) | English | Bemba | Nyanja |
| Tanzania (TZ) | Swahili | English | - |
| Kenya (KE) | English | Swahili | - |
| Rwanda (RW) | English | French | Kinyarwanda |
| Mozambique (MZ) | Portuguese | English | - |

## Translation Coverage

| Screen | EN | NY | SW | FR | PT | BEM |
|--------|----|----|----|----|----|-----|
| Login | x | x | x | x | x | x |
| Dashboard | x | x | x | x | x | x |
| Farms | x | x | x | x | x | x |
| Livestock | x | x | x | x | x | x |
| Marketplace | x | x | x | x | x | x |
| Finance | x | x | x | x | x | x |
| Reports | x | x | x | x | x | x |
| Profile | x | x | x | x | x | x |

## Adding a New Language

1. Create `values-{code}/strings.xml`
2. Translate all string resources
3. Add to CountryConfig in backend
4. Update this document

## Fallback Behavior
- App checks device language first
- Falls back to tenant default language
- Final fallback: English
