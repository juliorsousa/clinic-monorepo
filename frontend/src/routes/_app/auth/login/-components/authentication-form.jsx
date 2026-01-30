import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'
// import { useAuth } from '@/contexts/auth-provider'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link } from '@tanstack/react-router'
import { AxiosError } from 'axios'
import { Loader2, LogIn, Sparkles } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { toast } from 'sonner'
import { z } from 'zod'

const authenticateFormSchema = z.object({
  username: z
    .string()
    .min(3, { message: 'O usuário precisa ter pelo menos 3 caracteres.' }),
  password: z.string().min(1, { message: 'A senha é obrigatória.' }),
})

export function AuthenticationForm() {
  const form = useForm({
    resolver: zodResolver(authenticateFormSchema),
    reValidateMode: 'onSubmit',
    defaultValues: {
      username: '',
      password: '',
    },
  })

  // const { signIn } = useAuth()
  function signIn({ login, password }) {
    console.log(
      `Logging in with username: ${login} and password: ${password}`
    )
  }

  async function onHandleSubmit(data) {
    try {
      await signIn({
        login: data.username,
        password: data.password,
      })
    } catch (ex) {
      if (ex instanceof AxiosError) {
        toast.error(
          ex?.response?.data?.message ??
            'Ocorreu um erro interno no servidor. Por favor, contate um administrador.'
        )
      } else {
        toast.error(
          'Ocorreu um erro interno no servidor. Por favor, contate um administrador.'
        )
      }
    }
  }

  return (
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(onHandleSubmit)}
        className="flex flex-col gap-4"
      >
        <div className="flex flex-col gap-6">
          <FormField
            control={form.control}
            name="username"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Nome de usuário</FormLabel>

                <FormControl>
                  <Input
                    type="text"
                    placeholder="john@clinic.com"
                    className="border mt-1"
                    {...field}
                  />
                </FormControl>

                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="password"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Senha</FormLabel>

                <FormControl>
                  <Input
                    type="password"
                    placeholder={'•'.repeat(8)}
                    className="border mt-1"
                    {...field}
                  />
                </FormControl>

                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        <Link
          className="text-sm font-medium text-amber-500 hover:brightness-75 hover:underline transition w-fit underline"
          to="/recovery"
        >
          Esqueceu sua senha?
        </Link>

        <Button
          disabled={form.formState.isSubmitting}
          type="submit"
          variant="outline"
          className="border bg-transparent border-amber-700 hover:bg-amber-500"
        >
          {form.formState.isSubmitting ? (
            <Loader2 className="size-4 animate-spin" />
          ) : (
            <LogIn className="size-4" />
          )}
          Entrar
        </Button>

        <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
          <span className="text-muted-foreground relative z-10 px-2 bg-card">
            Não tem uma conta?
          </span>
        </div>

        <Link to="/auth/register">
          <Card className="flex p-4 gap-4 group bg-sidebar hover:brightness-75 transition-all">
            <Sparkles className="size-6 text-amber-700" />

            <div className="flex flex-col gap-1">
              <span className="text-sm font-medium text-amber-500 transition">
                Cadastre-se agora gratuitamente
              </span>
            </div>
          </Card>
        </Link>
      </form>
    </Form>
  )
}
