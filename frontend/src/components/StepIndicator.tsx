type StepIndicatorProps = { activeSteps: number }

const steps = [1, 2, 3]

export default function StepIndicator({ activeSteps }: StepIndicatorProps) {
  return (
    <div className="step-indicator" aria-hidden="true">
      {steps.map((step) => (
        <i key={step} className={step <= activeSteps ? 'active' : ''} />
      ))}
    </div>
  )
}
