package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main
{


    /* *** Essa Função le uma imagem ***
    *
    * @param arquivo Espera uma String com o pathFile do imagem Ex: "/src/imagem/picture.jpg"
    * @return um objeto da classe BufferedImage contendo, caso nao ache a imagem
    * é apontado para nulo
    */
    public static BufferedImage lerImagem (String arquivo)
    {
        BufferedImage imagem = null;
        try
        {
            imagem = ImageIO.read(new File(arquivo));
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
        return imagem;
    }

    /* *** Essa funcao abre um arquivo e escreve uma nova imagem, caso já exista uma ***
    *      ele sobrescreve ela
    * @param imagem A ser salva
    * @param arquivo Espera uma String com o pathFile do imagem Ex: "/src/imagem/picture.jpg"
    * @return retorna uma nova imagem no caminho descrito pelo @param arquivo, sobrescreve o
    * arquivo caso ele tenha o mesmo nome
    */
    public static void escreverImagem (BufferedImage imagem, String arquivo)
    {
        try
        {
            ImageIO.write(imagem , "png", new File(arquivo));

        }catch (IOException e)
        {
            System.out.println(e);
        }
    }


    /* *** Essa Função lê uma imagem e um texto qualquer e retorna essa imagem com o texto escondido usando LSB ***
    *
    *  *****  https://en.wikipedia.org/wiki/Least_significant_bit ******
    *
    * @param int bit vai receber o inteiro de um numero ASCII de uma letra  ex: 'b' <- 98 DEC
    * @param int pixelX e pixelY serão as coordenadas do imagem começando na posiçãp (0, 0)
    * @return retorna uma outra imagem alterada com os seus ultimos bits modificados
    *
    * ************************************************************************************************************
    * Breve explicação de como a função abaixo funciona para nao esquecer futuramente....
    *
    *   0110 1111 = 'o'
    *   *****pegando o bitChar*****
    *
    *   ‭0110 1111‬ & 0000 0001 = 0000 0001 == 1
    *   >>1
    *   0011 0111 & 0000 0001 = 0000 0001 == 1
    *   >>1
    *   0001 1011 & 0000 0001 = 0000 0001 == 1
    *   >>1
    *   0000 1101 & 0000 0001 = 0000 0000 == 0
    *   >>1
    *   0000 0110 & 0000 0001 = 0000 0000 == 0
    *   >>1
    *   0000 0011 & 0000 0001 = 0000 0001 == 1
    *   >>1
    *   0000 0001 & 0000 0001 = 0000 0001 == 1
    *   >>1
    *   0000 0000 & 0000 0001 = 0000 0000 == 0
    *
    *   *****transferindo o bitChar para imagem*****
    *
    *   se bitChar == 1
    *   imagem.getRGB(coordX, coordY) | 0x000000001 == ......1
    *   ex: -11.980.005 | 0x00000001
    *   1111 1111 0100 1001 0011 0011 0001 1011 | 0000 0000 0000 0000 0000 0000 0000 0001 =
    *   1111 1111 0100 1001 0011 0011 0001 1011
    *
    *   como pode ver espelhamos o binario, caso o binario da imagem terminasse em 0 a saida
    *   do ultimo binario da imagem seria 1, assim adicionando 1 ultimo bit
    *
    *   se bitChar == 0
    *   imagem.getRGB(coordX, coordY) | 0x000000001 == ......1
    *   ex: ‭4.282.790.941‬ & 0xFFFFFFFE
    *   ‭1111 1111 0100 0110 0011 0100 0001 1101‬ & ‭1111 1111 1111 1111 1111 1111 1111 1110‬ =
    *   1111 1111 0100 0110 0011 0100 0001 1100
    *
    */

    public static void escondeMensagem (String arquivo_entrada, String msg, String arquivo_saida)
    {
        int bitChar;
        int pixelX = 0;
        int pixelY = 0;
        BufferedImage imagem = lerImagem(arquivo_entrada);

        for (int i = 0; i < msg.length(); i++)
        {
            bitChar = (int) msg.charAt(i);                //pega o numero ASCII do caracter do texto
            for (int j = 0; j < 8; j++)
            {
                int flag = bitChar & 0x00000001;            //pega um digito do caracter Ex: 97 = 01100001 & 00000001 = 1
                if (flag == 1)
                {
                    if (pixelX < imagem.getWidth())
                    {
                        imagem.setRGB(pixelX, pixelY, imagem.getRGB(pixelX, pixelY) | 0x00000001);
                        pixelX++;

                    }else
                    {
                        pixelX = 0;
                        pixelY++;
                        imagem.setRGB(pixelX, pixelY, imagem.getRGB(pixelX, pixelY) | 0x00000001);
                    }
                }else
                {
                    if (pixelX < imagem.getWidth())
                    {
                        imagem.setRGB(pixelX, pixelY, imagem.getRGB(pixelX, pixelY) & 0xFFFFFFFE);
                        pixelX++;

                    }else
                    {
                        pixelX = 0;
                        pixelY++;
                        imagem.setRGB(pixelX, pixelY, imagem.getRGB(pixelX, pixelY) & 0xFFFFFFFE);
                    }
                }
                //depois de verificar e trocar os bits, andamos com o proximo bit no caracter
                bitChar = bitChar >> 1;
            }
        }
        escreverImagem(imagem, arquivo_saida);
    }

    /* *** Extrai informações da imagem ***
    *
    * @param Recebe uma imagem como paramentro
    * @param Recebe o tamanho da String usada para esconder o texto
    *
    * ************************************************************************************************************
    * Breve explicação de como a função abaixo funciona para nao esquecer futuramente....
    *
    *   *****extraindo o bitChar*****
    *   'o' so exemplo acima
    *
    *   loop de 0 a 7
    *   0)
    *   ‭1111 1111 0100 1001 0011 0011 0001 1011 & 0000 0000 0000 0000 0000 0000 0000 0001 =
    *   0000 0000 0000 0000 0000 0000 0000 0001 = 1
    *   1)
    *   1111 1111 0100 1011 0011 0101 0001 1101‬ & 0000 0000 0000 0000 0000 0000 0000 0001 =
    *   0000 0000 0000 0000 0000 0000 0000 0001 = 1
    *   2)
    *   ‭1111 1111 0100 1100 0011 1000 0010 0001 & ‬0000 0000 0000 0000 0000 0000 0000 0001 =
    *   0000 0000 0000 0000 0000 0000 0000 0001 = 1
    *   3)
    *   1111 1111 1111 1111 0100 0011 0111 0111‬ & 0000 0000 0000 0000 0000 0000 0000 0001 =
    *   0000 0000 0000 0000 0000 0000 0000 0001 = 1
    *   4)
    *   ‭1111 1111 0100 0110 0011 0100 0001 1100‬ & 0000 0000 0000 0000 0000 0000 0000 0001 =
    *   0000 0000 0000 0000 0000 0000 0000 0000 = 0
    *   5)
    *   1111 1111 0100 0011 0011 0001 0001 1001 & ‬0000 0000 0000 0000 0000 0000 0000 0001 =
    *   0000 0000 0000 0000 0000 0000 0000 0001 = 1
    *   6)
    *   1111 1111 0100 0001 0011 0001 0001 1011 & ‬‬0000 0000 0000 0000 0000 0000 0000 0001 =
    *   0000 0000 0000 0000 0000 0000 0000 0001 = 1
    *   7)
    *   ‭1111 1111 0100 0001 0011 0001 0001 1010‬ & 0000 0000 0000 0000 0000 0000 0000 0001 =
    *   0000 0000 0000 0000 0000 0000 0000 0000 = 0
    *   (0110 1111) familiar...
    *   ------------------------------------------------------------------------------------
    *   se bitChar == 1
    *   bit começa com 0 e é um inteiro necessário
    *   bit = 0 >> 1 = 0
    *   bit = 0 | 0x80
    *
    *   se bitChar == 0
    *   bit = bit  >> 1
    *
    *   0) bC == 1
    *   0000 0000 | 1000 0000 = 1000 0000
    *   bit 1000 0000 ou 128
    *
    *   1)bC == 1
    *   bit = 128 >> 1 = 64
    *   bit = 64 | 0x80
    *   0100 0000 | 1000 0000 = 1100 0000 (int)192
    *
    *   2)bC == 1
    *   bit = 192 >> 1 = 96
    *   bit = 96 | 0x80
    *   0110 0000 |1000 0000 = 1110 0000  (int)224
    *
    *   3)bC == 1
    *   bit = 224 >> 1 = 112
    *   bit = 112 | 0x80
    *   0111 0000 | 1000 0000 = 1111 0000 (int)240
    *
    *   4) bC == 0
    *   bit = 240 >> 1
    *   1111 0000 >> 1 = 0111 1000 (int)120
    *
    *   5) bC == 1
    *   bit = 120 >> 1
    *   bit = 60 | 0x80
    *   0011 1100 | 1000 0000 = 1011 1100 (int)188
    *
    *   6) bC == 1
    *   bit = 188 >> 1
    *   bit = 94 | 0x80
    *   0101 1110 | 1000 0000 = 1101 1110 (int)222
    *
    *   7)bC == 0
    8   bit = 222 >> 1
    *   1101 1111 >> 1 = 0110 1111 (int)111
    *   ------------------------------------------------------------------------------------
    *   (char)111 = 'o'
    *   FULL CIRCLE!!!!!
    */

    public static void recuperaMensagem (String arquivo, int tamanho)
    {
        int pixelX = 0;
        int pixelY = 0;
        int bitChar;
        char[] caracters = new char[tamanho];           //tamanho definido pela string na main
        BufferedImage imagem = lerImagem(arquivo);
        for (int i = 0; i < tamanho; i++)
        {
            int bit = 0;
            //
            for (int j = 0; j < 8; j++)
            {
                if (pixelX < imagem.getWidth())
                {
                    bitChar = imagem.getRGB(pixelX, pixelY) & 0x00000001;
                    pixelX++;

                }else
                {
                    pixelX = 0;
                    pixelY++;
                    bitChar = imagem.getRGB(pixelX, pixelY) & 0x00000001;
                }
                if (bitChar == 1)
                {
                    bit = bit >> 1;
                    bit = bit | 0x80; //0x80 = 1000 0000 nosso caracter em questao

                }else
                {
                    bit = bit >> 1;
                }
            }
            caracters[i] = (char)bit;
            System.out.print(caracters[i]);
        }
    }

    // TODO: 08/09/2017 Tentar esconder uma imagem dentro da imagem ...

    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        escondeMensagem("src/imagem/123.jpg", s,"src/imagem/321.jpg");
        recuperaMensagem("src/imagem/321.jpg", s.length());
    }
}
