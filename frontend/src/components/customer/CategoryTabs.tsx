import { customerMenuCategories } from '../../types'
import type { MenuCategory } from '../../types'

type CategoryTabsProps = {
  category: MenuCategory
  onSelect: (category: MenuCategory) => void
}

export default function CategoryTabs({ category, onSelect }: CategoryTabsProps) {
  return (
    <nav className="customer-tabs" aria-label="Menu categories">
      {customerMenuCategories.map((entry) => {
        const isActive = category === entry.id
        return (
          <button
            key={entry.id}
            className={`customer-tab ${isActive ? 'customer-tab-active' : ''}`}
            type="button"
            aria-pressed={isActive}
            onClick={() => onSelect(entry.id)}
          >
            {entry.label}
          </button>
        )
      })}
    </nav>
  )
}
