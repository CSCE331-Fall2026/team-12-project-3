type StepIndicatorProps = { activeSteps: number }

export default function StepIndicator({ activeSteps }: StepIndicatorProps) {
  return (
    <div className="step-indicator" aria-hidden="true">
      {[1, 2, 3].map((step) => <i key={step} className={step <= activeSteps ? 'active' : ''} />)}
    </div>
  )
}
