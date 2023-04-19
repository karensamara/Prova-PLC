--gerador de numeros aleatorios com o tempo atual do sistema
math.randomseed(os.time())

local function ataque()
    --usa o gerador aleatório pra dizer qual ataque será feito
    local valor = math.random(1, 20)
    --intervalos de valor para cada ataque disponivel e o dano do ataque
    if valor <= 10 then
        return "Choque do Trovão", 50
    elseif valor <= 15 then
        return "Calda de Ferro", 100
    elseif valor <= 18 then
        return "Investida Trovão", 150
    else
        return "Trovão", 200
    end
end

local function batalha(pokemon1, pokemon2)
    --cria uma coroutine pra controlar a exeucução da batalha
    local turno = coroutine.create(function()
        while pokemon1.hp > 0 and pokemon2.hp > 0 do
            local atkNome, atkDano = ataque()
            pokemon2.hp = pokemon2.hp - atkDano
            --printa o pokemon que atacou, o golpe que ele usou no outro e o hp restante de cada um
            print(string.format("%s usou %s em %s! (%s %d HP) (%s %d HP)", pokemon1.nome, atkNome, pokemon2.nome, pokemon1.nome, pokemon1.hp, pokemon2.nome, pokemon2.hp))
            --pausando a coroutine
            coroutine.yield()
            --inverte o papel dos 2 pokemons
            pokemon1, pokemon2 = pokemon2, pokemon1
        end
    end)

    return turno
end

local function iniciaBatalha(pokemon1, pokemon2)
    local turno = batalha(pokemon1, pokemon2)
    --enquanto o hp dos 2 pokemons for maior do que 0, continua, se nao para
    while coroutine.status(turno) ~= "dead" do
        coroutine.resume(turno)
    end

    if pokemon1.hp <= 0 then
        print(string.format("%s desmaiou! %s venceu a batalha!", pokemon1.nome, pokemon2.nome))
    else
        print(string.format("%s desmaiou! %s venceu a batalha!", pokemon2.nome, pokemon1.nome))
    end
end

local Pikachu = {nome = "Pikachu", hp = 800}
local Raichu = {nome = "Raichu", hp = 1000}

print("Quem vai começar a batalha? Digite 1 para Pikachu e 2 para Raichu:")
--le o valor digitado para escolher o pokemon
local escolha = tonumber(io.read())

if escolha == 1 then
    iniciaBatalha(Pikachu, Raichu)
elseif escolha == 2 then
    iniciaBatalha(Raichu, Pikachu)
else
    print("Escolha inválida!")
end