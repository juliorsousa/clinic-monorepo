import { createFileRoute } from '@tanstack/react-router'
import { AuthenticationForm } from './-components/authentication-form'
import coverImg from '@/assets/login-cover.png'
import logoImg from '@/assets/logo.png'
import { Card } from '@/components/ui/card'

export const Route = createFileRoute('/_app/auth/login/')({
  component: Authenticate,
  head: () => ({
    meta: [
      {
        title: 'Autentique-se | Clínica',
      },
    ],
  }),
})

export default function Authenticate() {
  return (
    <div className="h-screen w-full flex items-center justify-center bg-muted/30 p-6">
      <Card className="flex flex-row items-stretch w-full max-w-5xl h-[650px] overflow-hidden rounded-xl shadow-lg p-0">

        {/* LEFT IMAGE */}
        <div className="hidden md:flex w-1/2">
          <img
            src={coverImg}
            alt="Login cover"
            className="w-full h-full object-cover"
          />
        </div>

        {/* RIGHT CONTENT */}
        <div className="flex w-full md:w-1/2 flex-col justify-center px-10">
          <div className="w-full max-w-sm mx-auto flex flex-col gap-6">

            <header className="flex flex-col gap-4 items-center">
              <img
                src={logoImg}
                alt="Clinic Logo"
                width={64}
                height={64}
              />

              <div className="text-center space-y-1">
                <h2 className="text-2xl font-bold">
                  Clínica IFB-A
                </h2>

                <p className="text-muted-foreground text-sm">
                  Referência em cuidado integral à saúde, oferecendo atendimento humanizado, ético e de qualidade.
                </p>
              </div>
            </header>

            <AuthenticationForm />
          </div>
        </div>

      </Card>
    </div>
  )
}
