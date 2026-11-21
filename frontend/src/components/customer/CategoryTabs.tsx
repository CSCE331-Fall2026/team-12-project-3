import { customerMenuCategories } from '../../types'
import type { MenuCategory } from '../../types'

type CategoryTabsProps = {
  category: MenuCategory
  onSelect: (category: MenuCategory) => void
}

export default function CategoryTabs({ category, onSelect }: CategoryTabsProps) {
  return (
    <nav className="customer-tabs" aria-label="Menu categories">
      {customerMenuCategories.map((entry) => (
        <button
          key={entry.id}
          className={`customer-tab ${category === entry.id ? 'customer-tab-active' : ''}`}
          type="button"
          aria-pressed={category === entry.id}
          onClick={() => onSelect(entry.id)}
        >
          {entry.label}
        </button>
      ))}
    </nav>
  )
}
